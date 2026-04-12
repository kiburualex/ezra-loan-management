package world.ezra.loan_management.repayment.internal.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Alex Kiburu
 */
public record RepaymentResponse(
        Long id,
        Long loanId,
        Long installmentId,
        BigDecimal amount,
        LocalDateTime paymentDate,
        String paymentMethod,
        String message
) {
}
