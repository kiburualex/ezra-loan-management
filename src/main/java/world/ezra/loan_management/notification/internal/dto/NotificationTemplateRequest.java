package world.ezra.loan_management.notification.internal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import world.ezra.loan_management.common.enums.PreferredChannel;

/**
 * @author Alex Kiburu
 */
public record NotificationTemplateRequest(
        @NotBlank(message = "Event type is required")
        String eventType,

        @NotNull(message = "Channel is required")
        PreferredChannel channel,

        String subject,

        @NotBlank(message = "Body is required")
        String body,

        Long productId
) {
}
