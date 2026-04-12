package world.ezra.loan_management.loan.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.ezra.loan_management.common.enums.FeeType;
import world.ezra.loan_management.common.enums.LoanFeeType;
import world.ezra.loan_management.loan.internal.model.Loan;
import world.ezra.loan_management.loan.internal.model.LoanFee;
import world.ezra.loan_management.loan.internal.model.LoanInstallment;
import world.ezra.loan_management.loan.internal.repository.LoanFeeRepository;
import world.ezra.loan_management.product.internal.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

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
     * Calculate late fee for an overdue installment
     */
    public BigDecimal calculateLateFee(LoanInstallment installment, Product product, long daysOverdue) {
        // Check if late fee should be applied
        if (daysOverdue < product.getDaysAfterDueForLateFee()) {
            return BigDecimal.ZERO;
        }

        if (product.getLateFeeType() == null || product.getLateFeeValue() == null) {
            return BigDecimal.ZERO;
        }

        return calculateFee(
                installment.getAmountDue(),
                product.getLateFeeType(),
                product.getLateFeeValue()
        );
    }

    /**
     * Apply late fee to an overdue installment (saves to database)
     */
    @Transactional
    public BigDecimal applyLateFeeToInstallment(Loan loan, LoanInstallment installment, Product product, long daysOverdue) {
        BigDecimal lateFee = calculateLateFee(installment, product, daysOverdue);

        if (lateFee.compareTo(BigDecimal.ZERO) > 0) {
            // Check if fee already applied for this installment
            boolean feeAlreadyApplied = loanFeeRepository
                    .findByLoanIdAndFeeType(loan.getId(), LoanFeeType.LATE)
                    .stream()
                    .anyMatch(fee -> fee.getReason() != null &&
                            fee.getReason().contains("installment " + installment.getInstallmentNumber()));

            if (!feeAlreadyApplied) {
                LoanFee fee = LoanFee.builder()
                        .loan(loan)
                        .feeType(LoanFeeType.LATE)
                        .amount(lateFee)
                        .reason(String.format("Late fee for installment #%d due on %s (overdue by %d days)",
                                installment.getInstallmentNumber(), installment.getDueDate(), daysOverdue))
                        .build();
                loanFeeRepository.save(fee);
                log.info("Applied late fee of {} to loan {} for installment {}",
                        lateFee, loan.getId(), installment.getInstallmentNumber());
                return lateFee;
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * Calculate daily fee for an overdue loan
     */
    public BigDecimal calculateDailyFee(Loan loan, Product product, long daysOverdue) {
        // Check if daily fee is configured
        if (product.getDailyFeeType() == null || product.getDailyFeeValue() == null) {
            return BigDecimal.ZERO;
        }

        Integer daysAfterDue = product.getDaysAfterDueForDailyFee();
        if (daysAfterDue == null || daysOverdue < daysAfterDue) {
            return BigDecimal.ZERO;
        }

        return calculateFee(
                loan.getPrincipalAmount(),
                product.getDailyFeeType(),
                product.getDailyFeeValue()
        );
    }

    /**
     * Apply daily fee to an overdue loan (saves to database)
     * Returns the fee amount applied, or ZERO if no fee was applied
     */
    @Transactional
    public BigDecimal applyDailyFeeToLoan(Loan loan, Product product, long daysOverdue) {
        BigDecimal dailyFee = calculateDailyFee(loan, product, daysOverdue);

        if (dailyFee.compareTo(BigDecimal.ZERO) > 0) {
            // Check if daily fee already applied today
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            boolean feeAppliedToday = loanFeeRepository
                    .findByLoanIdAndFeeType(loan.getId(), LoanFeeType.DAILY)
                    .stream()
                    .anyMatch(fee -> fee.getAppliedDate().isAfter(startOfDay));

            if (!feeAppliedToday) {
                LoanFee fee = LoanFee.builder()
                        .loan(loan)
                        .feeType(LoanFeeType.DAILY)
                        .amount(dailyFee)
                        .reason(String.format("Daily fee for day %d of overdue", daysOverdue))
                        .build();
                loanFeeRepository.save(fee);
                log.info("Applied daily fee of {} to loan {} (day {})",
                        dailyFee, loan.getId(), daysOverdue);
                return dailyFee;
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * Check if late fee has already been applied for an installment
     */
    public boolean isLateFeeApplied(Loan loan, LoanInstallment installment) {
        return loanFeeRepository
                .findByLoanIdAndFeeType(loan.getId(), LoanFeeType.LATE)
                .stream()
                .anyMatch(fee -> fee.getReason() != null &&
                        fee.getReason().contains("installment " + installment.getInstallmentNumber()));
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