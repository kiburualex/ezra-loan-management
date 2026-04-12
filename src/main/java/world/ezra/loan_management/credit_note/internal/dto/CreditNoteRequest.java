package world.ezra.loan_management.credit_note.internal.dto;

import jakarta.validation.constraints.NotNull;
import world.ezra.loan_management.credit_note.internal.enums.CreditNoteReason;

import java.math.BigDecimal;

/**
 * @author Alex Kiburu
 */
public record CreditNoteRequest(
        @NotNull(message = "Loan ID [loanId] is required")
        Long loanId,
        @NotNull(message = "Amount [amount] is required")
        BigDecimal amount,
        @NotNull(message = "Reason [reason] is required")
        CreditNoteReason reason
) {
}
