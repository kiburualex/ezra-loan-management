package world.ezra.loan_management.loan.internal.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.ezra.loan_management.common.dto.GenericResponse;
import world.ezra.loan_management.common.dto.PaginatedResponse;
import world.ezra.loan_management.common.enums.BillingType;
import world.ezra.loan_management.common.enums.LoanStatus;
import world.ezra.loan_management.common.enums.TenureType;
import world.ezra.loan_management.common.exceptions.OperationNotPermittedException;
import world.ezra.loan_management.customer.api.CustomerApi;
import world.ezra.loan_management.customer.internal.model.Customer;
import world.ezra.loan_management.loan.api.LoanApi;
import world.ezra.loan_management.loan.internal.dto.LoanRequest;
import world.ezra.loan_management.loan.internal.model.Loan;
import world.ezra.loan_management.loan.internal.model.LoanInstallment;
import world.ezra.loan_management.loan.internal.repository.LoanRepository;
import world.ezra.loan_management.product.api.ProductApi;
import world.ezra.loan_management.product.internal.model.Product;
import world.ezra.loan_management.scoring.api.CreditScoringApi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanApi {
    private final LoanRepository loanRepository;
    private final LoanInstallmentService installmentService;
    private final CustomerApi customerApi;
    private final ProductApi productApi;
    private final CreditScoringApi creditScoringApi;
    private final FeeApplicationService feeApplicationService;
    private final Gson gson = new Gson();

    @Override
    public ResponseEntity<?> findAll(String searchTerm, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<@NonNull Loan> customerPage = loanRepository.findAll(pageable);
        return ResponseEntity.ok(new PaginatedResponse<>(customerPage));
    }

    @Transactional
    @Override
    public ResponseEntity<?> newLoanApplication(LoanRequest request) {
        log.info("Creating loan for customer: {}, product: {}",
                request.customerId(), request.productId());

        // 1. Check if customer exists
        Customer customer = customerApi.findById(request.customerId())
                .orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + request.customerId()));

        // 2. Check if product exists
        Product product = productApi.findById(request.productId())
                .orElseThrow(() -> new NoSuchElementException("Product not found with ID: " + request.productId()));

        // 3. Check if product is active
        if (!product.getActive()) {
            throw new OperationNotPermittedException("Product '" + product.getName() + "' is not active for loans");
        }

        // 4. Validate loan amount against product limits
        if (request.principalAmount().compareTo(product.getMinLoanAmount()) < 0) {
            throw new OperationNotPermittedException(
                    String.format("Principal amount %.2f is below minimum loan amount of %.2f",
                            request.principalAmount(), product.getMinLoanAmount()));
        }

        if (request.principalAmount().compareTo(product.getMaxLoanAmount()) > 0) {
            throw new OperationNotPermittedException(
                    String.format("Principal amount %.2f exceeds maximum loan amount of %.2f",
                            request.principalAmount(), product.getMaxLoanAmount()));
        }

        // 5. Perform credit scoring
        var creditDecision = creditScoringApi.calculateScore(customer, product, request.principalAmount());

        // 6. Check if loan should be approved based on score
        if ("REJECT".equals(creditDecision.decision())) {
            throw new OperationNotPermittedException(
                    String.format("Loan application rejected. Credit score: %d. Reason: %s",
                            creditDecision.score(), creditDecision.notes()));
        }

        if ("REVIEW".equals(creditDecision.decision())) {
            throw new OperationNotPermittedException(
                    String.format("Borderline score, requires manual review. Credit score: %d. Reason: %s",
                            creditDecision.score(), creditDecision.notes()));
        }

        // 7. Calculate interest amount
        BigDecimal interestAmount = calculateInterest(request.principalAmount(), product);

        // 8. Calculate service fee (without loan yet)
        BigDecimal serviceFee = feeApplicationService.calculateServiceFee(product, request.principalAmount());

        // 9. Calculate total repayable amount (principal + interest + fees)
        BigDecimal totalRepayable = request.principalAmount()
                .add(interestAmount)
                .add(serviceFee);

        // 10. Create loan entity
        Loan loan = Loan.builder()
                .customer(customer)
                .product(product)
                .principalAmount(request.principalAmount())
                .disbursedAmount(request.disbursedAmount())
                .totalRepayableAmount(totalRepayable)
                .creditScore(creditDecision.score())
                .isInstallmentBased(request.isInstallmentBased() != null ? request.isInstallmentBased() : false)
                .numberOfInstallments(request.numberOfInstallments())
                .billingType(request.billingType())
                .consolidatedDueDay(request.consolidatedDueDay())
                .status(LoanStatus.OPEN)
                .build();

        // Validate loan rules before saving
        validateLoanRules(loan);

        Loan savedLoan = loanRepository.save(loan);

        // 11. Apply service fee to the saved loan (persist to database)
        BigDecimal appliedServiceFee = feeApplicationService.applyServiceFeeToLoan(savedLoan, product, request.principalAmount());

        // 12. Update total repayable if fees were applied (should already match, but double-check)
        if (appliedServiceFee.compareTo(BigDecimal.ZERO) > 0) {
            // Fee already included in totalRepayable, but ensure it's consistent
            log.info("Service fee of {} applied to loan {}", appliedServiceFee, savedLoan.getId());
        }

        // 13. Create installments for the loan
        List<LoanInstallment> installments = installmentService.createInstallments(savedLoan);

        log.info("Loan created successfully with ID: {}, Installments: {}, Credit Score: {}, Service Fee: {}",
                savedLoan.getId(), installments.size(), creditDecision.score(), appliedServiceFee);

        Map<String, Object> loanDetails = Map.of(
                "loan", savedLoan,
                "serviceFee", appliedServiceFee
        );

        GenericResponse genericResponse = GenericResponse.builder()
                .status("00")
                .message("Loan created successfully")
                .transactionId(savedLoan.getId().toString())
                .data(loanDetails)
                .build();
        return ResponseEntity.ok().body(genericResponse);
    }

    /**
     * Calculate total interest based on product configuration
     */
    private BigDecimal calculateInterest(BigDecimal principal, Product product) {
        BigDecimal interestRate = product.getInterestRate();

        // Convert percentage to decimal (12.50 -> 0.125)
        BigDecimal rateAsDecimal = interestRate.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);

        // Calculate based on tenure
        BigDecimal interest;
        if (product.getTenureType() == TenureType.MONTHS) {
            // Simple interest for months
            interest = principal.multiply(rateAsDecimal)
                    .multiply(BigDecimal.valueOf(product.getTenureValue()))
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        } else {
            // Daily interest
            interest = principal.multiply(rateAsDecimal)
                    .multiply(BigDecimal.valueOf(product.getTenureValue()))
                    .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        }

        return interest;
    }

    /**
     * Validate business rules before saving loan
     */
    private void validateLoanRules(Loan loan) {
        // Validate installment-based rules
        if (loan.getIsInstallmentBased() && (loan.getNumberOfInstallments() == null || loan.getNumberOfInstallments() <= 0)) {
            throw new IllegalArgumentException("Number of installments is required for installment-based loans");
        }

        if (!loan.getIsInstallmentBased() && loan.getNumberOfInstallments() != null) {
            throw new IllegalArgumentException("Number of installments should be null for lump-sum loans");
        }

        // Validate consolidated billing
        if (loan.getBillingType() == BillingType.CONSOLIDATED && loan.getConsolidatedDueDay() == null) {
            throw new IllegalArgumentException("Consolidated due day is required for consolidated billing");
        }

        if (loan.getBillingType() == BillingType.CONSOLIDATED &&
                (loan.getConsolidatedDueDay() < 1 || loan.getConsolidatedDueDay() > 31)) {
            throw new IllegalArgumentException("Consolidated due day must be between 1 and 31");
        }
    }
}
