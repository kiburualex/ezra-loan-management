package world.ezra.loan_management.common.dto;

import java.math.BigDecimal;

/**
 * @author Alex Kiburu
 */
public record LoanCreationRequestEvent(
        Long loanId,                // Useful for tracing/logging
        Long productId,                // Useful for tracing/logging
        Long customerId,            // Maps to {customer_name}
        String phoneNumber,         // Required if the type is 'SMS'
        String customerName,        // Maps to {customer_name}
        BigDecimal principalAmount, // Maps to {principal_amount}
        BigDecimal totalRepayable,  // Maps to {total_repayable}
        String preferredChannel     // SMS, EMAIL
) {
}
