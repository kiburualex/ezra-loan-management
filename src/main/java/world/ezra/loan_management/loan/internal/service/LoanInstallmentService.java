package world.ezra.loan_management.loan.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.ezra.loan_management.common.enums.BillingType;
import world.ezra.loan_management.common.enums.InstallmentStatus;
import world.ezra.loan_management.loan.internal.dto.LoanRequest;
import world.ezra.loan_management.loan.internal.model.Loan;
import world.ezra.loan_management.loan.internal.model.LoanInstallment;
import world.ezra.loan_management.loan.internal.repository.LoanInstallmentRepository;
import world.ezra.loan_management.product.internal.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanInstallmentService {
    private final LoanInstallmentRepository installmentRepository;

    /**
     * Create installments for a loan
     */
    @Transactional
    public List<LoanInstallment> createInstallments(Loan loan) {
        log.info("Creating installments for loan ID: {}", loan.getId());

        List<LoanInstallment> installments = new ArrayList<>();

        // Case 1: Not installment-based (single payment)
        if (!Boolean.TRUE.equals(loan.getIsInstallmentBased())) {
            LoanInstallment singleInstallment = createSingleInstallment(loan);
            installments.add(singleInstallment);
        }
        // Case 2: Installment-based loan
        else {
            Integer numberOfInstallments = loan.getNumberOfInstallments();
            if (numberOfInstallments == null || numberOfInstallments <= 0) {
                throw new IllegalStateException("Number of installments must be specified for installment-based loans");
            }

            installments = createMultipleInstallments(loan, numberOfInstallments);
        }

        return installmentRepository.saveAll(installments);
    }

    /**
     * Create single installment for lump-sum payment
     */
    private LoanInstallment createSingleInstallment(Loan loan) {
        // Calculate due date based on product tenure
        LocalDate dueDate = calculateDueDate(loan);

        return LoanInstallment.builder()
                .loan(loan)
                .installmentNumber(1)
                .dueDate(dueDate)
                .amountDue(loan.getTotalRepayableAmount())
                .amountPaid(BigDecimal.ZERO)
                .status(InstallmentStatus.PENDING)
                .build();
    }

    /**
     * Create multiple installments for installment-based loan
     */
    private List<LoanInstallment> createMultipleInstallments(Loan loan, Integer numberOfInstallments) {
        List<LoanInstallment> installments = new ArrayList<>();

        // Calculate equal installment amount
        BigDecimal installmentAmount = loan.getTotalRepayableAmount()
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);

        // Adjust for rounding (add remainder to first installment)
        BigDecimal totalCalculated = installmentAmount.multiply(BigDecimal.valueOf(numberOfInstallments));
        BigDecimal remainder = loan.getTotalRepayableAmount().subtract(totalCalculated);

        LocalDate currentDueDate = calculateDueDate(loan);

        for (int i = 1; i <= numberOfInstallments; i++) {
            BigDecimal amountDue = installmentAmount;

            // Add remainder to first installment
            if (i == 1 && remainder.compareTo(BigDecimal.ZERO) > 0) {
                amountDue = amountDue.add(remainder);
            }

            LoanInstallment installment = LoanInstallment.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .dueDate(currentDueDate)
                    .amountDue(amountDue)
                    .amountPaid(BigDecimal.ZERO)
                    .status(InstallmentStatus.PENDING)
                    .build();

            installments.add(installment);

            // Calculate next due date based on billing type
            currentDueDate = calculateNextDueDate(loan, currentDueDate);
        }

        return installments;
    }

    /**
     * Calculate initial due date based on loan origination and product tenure
     */
    private LocalDate calculateDueDate(Loan loan) {
        LocalDate originationDate = loan.getOriginationDate().toLocalDate();
        Product product = loan.getProduct();

        return switch (product.getTenureType()) {
            case DAYS -> originationDate.plusDays(product.getTenureValue());
            case MONTHS -> originationDate.plusMonths(product.getTenureValue());
        };
    }

    /**
     * Calculate next due date based on billing type
     */
    private LocalDate calculateNextDueDate(Loan loan, LocalDate currentDueDate) {
        if (loan.getBillingType() == BillingType.CONSOLIDATED && loan.getConsolidatedDueDay() != null) {
            // Consolidated billing: all due on same day of month
            int dueDay = loan.getConsolidatedDueDay();
            LocalDate nextDueDate = currentDueDate.plusMonths(1);

            // Adjust to the due day
            if (nextDueDate.getDayOfMonth() != dueDay) {
                if (dueDay <= nextDueDate.lengthOfMonth()) {
                    nextDueDate = nextDueDate.withDayOfMonth(dueDay);
                } else {
                    nextDueDate = nextDueDate.withDayOfMonth(nextDueDate.lengthOfMonth());
                }
            }
            return nextDueDate;
        } else {
            // Individual billing: each installment due monthly from origination
            return currentDueDate.plusMonths(1);
        }
    }

    /**
     * Update overdue installments (run daily via scheduler)
     */
    @Transactional
    public int updateOverdueInstallments() {
        LocalDate today = LocalDate.now();
        int updated = installmentRepository.updateOverdueStatus(today);
        if (updated > 0) {
            log.info("Updated {} installments to OVERDUE status", updated);
        }
        return updated;
    }

    /**
     * Check if loan has any overdue installments
     */
    public boolean hasOverdueInstallments(Loan loan) {
        return installmentRepository.hasOverdueInstallments(loan, LocalDate.now());
    }

}
