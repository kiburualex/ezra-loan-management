package world.ezra.loan_management.notification.internal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import world.ezra.loan_management.common.enums.PreferredChannel;

/**
 * @author Alex Kiburu
 */
public record NotificationRequest(
        @NotNull(message = "Channel is required")
        PreferredChannel channel,

        @NotBlank(message = "Message is required")
        String message,

        Long customerId,
        Long loanId
) {
}
