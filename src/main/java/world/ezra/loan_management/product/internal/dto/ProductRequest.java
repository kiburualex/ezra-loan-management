package world.ezra.loan_management.product.internal.dto;

import jakarta.validation.constraints.*;
import world.ezra.loan_management.common.enums.FeeType;
import world.ezra.loan_management.common.enums.TenureType;

import java.math.BigDecimal;

/**
 * @author Alex Kiburu
 */
public record ProductRequest(
        @NotBlank(message = "Product name is required")
        @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotNull(message = "Tenure type is required")
        TenureType tenureType,

        @NotNull(message = "Tenure value is required")
        @Min(value = 1, message = "Tenure value must be at least 1")
        @Max(value = 360, message = "Tenure value cannot exceed 360")
        Integer tenureValue,

        @NotNull(message = "Interest rate is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Interest rate must be >= 0")
        @DecimalMax(value = "100.0", inclusive = true, message = "Interest rate must be <= 100")
        BigDecimal interestRate,

        @NotNull(message = "Service fee type is required")
        FeeType serviceFeeType,

        @NotNull(message = "Service fee value is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Service fee value must be >= 0")
        @DecimalMax(value = "1000000.0", message = "Service fee value cannot exceed 1,000,000")
        @Digits(integer = 13, fraction = 2, message = "Service fee value must have up to 13 integer digits and 2 fraction digits")
        BigDecimal serviceFeeValue,

        FeeType dailyFeeType,

        @DecimalMin(value = "0.0", inclusive = true, message = "Daily fee value must be >= 0")
        @DecimalMax(value = "1000000.0", message = "Daily fee value cannot exceed 1,000,000")
        BigDecimal dailyFeeValue,

        @NotNull(message = "Late fee type is required")
        FeeType lateFeeType,

        @NotNull(message = "Late fee value is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Late fee value must be >= 0")
        @DecimalMax(value = "1000000.0", message = "Late fee value cannot exceed 1,000,000")
        BigDecimal lateFeeValue,

        @NotNull(message = "Days after due for late fee is required")
        @Min(value = 0, message = "Days after due for late fee must be >= 0")
        @Max(value = 365, message = "Days after due for late fee cannot exceed 365")
        Integer daysAfterDueForLateFee,

        @Min(value = 0, message = "Days after due for daily fee must be >= 0")
        @Max(value = 365, message = "Days after due for daily fee cannot exceed 365")
        Integer daysAfterDueForDailyFee,

        @NotNull(message = "Minimum loan amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Minimum loan amount must be > 0")
        @DecimalMax(value = "10000000.0", message = "Minimum loan amount cannot exceed 10,000,000")
        BigDecimal minLoanAmount,

        @NotNull(message = "Maximum loan amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Maximum loan amount must be > 0")
        @DecimalMax(value = "10000000.0", message = "Maximum loan amount cannot exceed 10,000,000")
        BigDecimal maxLoanAmount,

        Boolean active

) {
    // Custom validation to ensure maxLoanAmount > minLoanAmount
    @AssertTrue(message = "Maximum loan amount must be greater than minimum loan amount")
    public boolean isMaxLoanAmountGreaterThanMin() {
        if (minLoanAmount == null || maxLoanAmount == null) {
            return true; // Let @NotNull handle null cases
        }
        return maxLoanAmount.compareTo(minLoanAmount) > 0;
    }

    // Custom validation for daily fee consistency
    @AssertTrue(message = "Daily fee value is required when daily fee type is specified")
    public boolean isDailyFeeValuePresentWhenTypeSpecified() {
        if (dailyFeeType != null && dailyFeeValue == null) {
            return false;
        }
        return true;
    }

    // Custom validation for daily fee type when value is present
    @AssertTrue(message = "Daily fee type is required when daily fee value is specified")
    public boolean isDailyFeeTypePresentWhenValueSpecified() {
        if (dailyFeeValue != null && dailyFeeType == null) {
            return false;
        }
        return true;
    }

    // Validate percentage values are within reasonable range
    @AssertTrue(message = "Interest rate as percentage should be between 0 and 100")
    public boolean isValidInterestRate() {
        if (interestRate == null) return true;
        return interestRate.compareTo(BigDecimal.ZERO) >= 0 &&
                interestRate.compareTo(new BigDecimal("100")) <= 0;
    }

    // Validate that daily fee percentage is reasonable
    @AssertTrue(message = "Daily fee percentage should be between 0 and 10")
    public boolean isValidDailyFeePercentage() {
        if (dailyFeeType == FeeType.PERCENTAGE && dailyFeeValue != null) {
            return dailyFeeValue.compareTo(BigDecimal.ZERO) >= 0 &&
                    dailyFeeValue.compareTo(new BigDecimal("10")) <= 0;
        }
        return true;
    }

    // Validate that late fee percentage is reasonable
    @AssertTrue(message = "Late fee percentage should be between 0 and 100")
    public boolean isValidLateFeePercentage() {
        if (lateFeeType == FeeType.PERCENTAGE && lateFeeValue != null) {
            return lateFeeValue.compareTo(BigDecimal.ZERO) >= 0 &&
                    lateFeeValue.compareTo(new BigDecimal("100")) <= 0;
        }
        return true;
    }
}
