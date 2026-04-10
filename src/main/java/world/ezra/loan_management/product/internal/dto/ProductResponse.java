package world.ezra.loan_management.product.internal.dto;

import world.ezra.loan_management.common.enums.FeeType;
import world.ezra.loan_management.common.enums.TenureType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Alex Kiburu
 */
public record ProductResponse(
        Long id,
        String name,
        String description,
        TenureType tenureType,
        Integer tenureValue,
        BigDecimal interestRate,
        FeeType serviceFeeType,
        BigDecimal serviceFeeValue,
        FeeType dailyFeeType,
        BigDecimal dailyFeeValue,
        FeeType lateFeeType,
        BigDecimal lateFeeValue,
        Integer daysAfterDueForLateFee,
        Integer daysAfterDueForDailyFee,
        BigDecimal minLoanAmount,
        BigDecimal maxLoanAmount,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
