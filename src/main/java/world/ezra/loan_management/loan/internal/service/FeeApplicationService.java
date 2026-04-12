package world.ezra.loan_management.loan.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.ezra.loan_management.common.enums.FeeType;
import world.ezra.loan_management.common.enums.LoanFeeType;
import world.ezra.loan_management.loan.internal.model.Loan;
import world.ezra.loan_management.loan.internal.model.LoanFee;
import world.ezra.loan_management.loan.internal.repository.LoanFeeRepository;
import world.ezra.loan_management.product.internal.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeeApplicationService {

    private final LoanFeeRepository loanFeeRepository;


    /**
     * Calculate service fee amount (without saving to database)
     */
    public BigDecimal calculateServiceFee(Product product, BigDecimal principalAmount) {
        if (product.getServiceFeeType() == null || product.getServiceFeeValue() == null) {
            return BigDecimal.ZERO;
        }

        return calculateFee(
                principalAmount,
                product.getServiceFeeType(),
                product.getServiceFeeValue()
        );
    }

    /**
     * Apply service fee to an existing loan (saves to database)
     */
    @Transactional
    public BigDecimal applyServiceFeeToLoan(Loan loan, Product product, BigDecimal principalAmount) {
        BigDecimal serviceFee = calculateServiceFee(product, principalAmount);

        if (serviceFee.compareTo(BigDecimal.ZERO) > 0) {
            LoanFee fee = LoanFee.builder()
                    .loan(loan)
                    .feeType(LoanFeeType.SERVICE)
                    .amount(serviceFee)
                    .reason(String.format("Service fee for loan origination (Product: %s)", product.getName()))
                    .build();
            loanFeeRepository.save(fee);
            log.info("Applied service fee of {} to loan {}", serviceFee, loan.getId());
        }

        return serviceFee;
    }

    /**
     * Calculate fee based on type (FIXED or PERCENTAGE)
     */
    private BigDecimal calculateFee(BigDecimal baseAmount, FeeType feeType, BigDecimal feeValue) {
        if (feeValue == null) {
            return BigDecimal.ZERO;
        }

        if (feeType == FeeType.FIXED) {
            return feeValue;
        } else { // PERCENTAGE
            return baseAmount.multiply(feeValue)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
    }
}