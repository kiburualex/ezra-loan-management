package world.ezra.loan_management.notification.api;

import world.ezra.loan_management.notification.internal.dto.NotificationRequest;
import world.ezra.loan_management.notification.internal.model.Notification;

/**
 * @author Alex Kiburu
 */
public interface NotificationApi {
    Notification sendNotification(NotificationRequest request);
}
