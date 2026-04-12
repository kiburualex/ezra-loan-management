package world.ezra.loan_management.notification.listeners;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;
import world.ezra.loan_management.common.dto.LoanCreationRequestEvent;
import world.ezra.loan_management.notification.api.NotificationApi;
import world.ezra.loan_management.notification.internal.dto.NotificationRequest;
import world.ezra.loan_management.notification.internal.model.NotificationTemplate;
import world.ezra.loan_management.notification.internal.repository.NotificationTemplateRepository;

import java.util.Optional;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoanNotificationListener {
    private final NotificationApi notificationApi;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final Gson gson = new Gson();

    @PulsarListener(
            topics = "loan-creation-topic",
            subscriptionName = "loan-mgmt-subscription",
            subscriptionType = SubscriptionType.Shared
    )
    public void handleLoanCreationNotificationRequest(LoanCreationRequestEvent event) {
        log.info(">>> Loan Creation event: {}", gson.toJson(event));
        /**
         * TODO: add event types as LOAN_CREATION, DUE_DATE, OTHERS
         * fetch template if there's no product but there's event type
         */
        // get notification template based on product and LoanCreation
        Optional<NotificationTemplate> notificationTemplateOptional = notificationTemplateRepository.
                findFirstByProductIdAndEventType(event.productId(), "LOAN_CREATION");
        if(notificationTemplateOptional.isPresent()) {
            NotificationTemplate notificationTemplate = notificationTemplateOptional.get();
            var message = notificationTemplate.getBody()
                    .replace("{customer_name}", event.customerName())
                    .replace("{principal_amount}", event.principalAmount().toString())
                    .replace("{total_repayable}", event.totalRepayable().toString());
            NotificationRequest newNotificationRequest = new NotificationRequest(
                    notificationTemplate.getChannel(),
                    message,
                    event.customerId(),
                    event.loanId()
            );

            notificationApi.sendNotification(newNotificationRequest);

        }
    }
}
