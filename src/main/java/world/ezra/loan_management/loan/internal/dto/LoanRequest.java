package world.ezra.loan_management.loan.internal.dto;

import jakarta.validation.constraints.*;
import world.ezra.loan_management.common.enums.BillingType;

import java.math.BigDecimal;

/**
 * @author Alex Kiburu
 */
public record LoanRequest(

        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "Principal amount is required")
        @DecimalMin(value = "0.01", message = "Principal amount must be greater than 0")
        @DecimalMax(value = "10000000.00", message = "Principal amount cannot exceed 10,000,000")
        BigDecimal principalAmount,

        @NotNull(message = "Disbursed amount is required")
        @DecimalMin(value = "0.01", message = "Disbursed amount must be greater than 0")
        BigDecimal disbursedAmount,

        @NotNull(message = "Total repayable amount is required")
        @DecimalMin(value = "0.01", message = "Total repayable amount must be greater than 0")
        BigDecimal totalRepayableAmount,

        Boolean isInstallmentBased,

        @Min(value = 1, message = "Number of installments must be at least 1")
        @Max(value = 360, message = "Number of installments cannot exceed 360")
        Integer numberOfInstallments,

        @NotNull(message = "Billing type is required")
        BillingType billingType,

        @Min(value = 1, message = "Consolidated due day must be between 1 and 31")
        @Max(value = 31, message = "Consolidated due day must be between 1 and 31")
        Integer consolidatedDueDay

) {
    // Custom validation for consolidated billing
    @AssertTrue(message = "Consolidated due day is required for consolidated billing")
    public boolean isValidConsolidatedDueDay() {
        if (billingType == BillingType.CONSOLIDATED && consolidatedDueDay == null) {
            return false;
        }
        return true;
    }

    // Custom validation for installment-based loans
    @AssertTrue(message = "Number of installments is required for installment-based loans")
    public boolean isValidNumberOfInstallments() {
        if (Boolean.TRUE.equals(isInstallmentBased) && numberOfInstallments == null) {
            return false;
        }
        return true;
    }

    // Custom validation for non-installment loans
    @AssertTrue(message = "Number of installments must be null for non-installment loans")
    public boolean isNumberOfInstallmentsNullForNonInstallment() {
        if (Boolean.FALSE.equals(isInstallmentBased) && numberOfInstallments != null) {
            return false;
        }
        return true;
    }
}