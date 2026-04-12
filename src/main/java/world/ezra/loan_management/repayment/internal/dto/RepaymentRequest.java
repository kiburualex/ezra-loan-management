package world.ezra.loan_management.repayment.internal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * @author Alex Kiburu
 */
public record RepaymentRequest(

        @NotNull(message = "Loan ID is required")
        Long loanId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        String paymentMethod

) {}
