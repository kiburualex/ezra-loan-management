package world.ezra.loan_management.notification.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import world.ezra.loan_management.notification.api.NotificationApi;
import world.ezra.loan_management.notification.internal.dto.NotificationRequest;
import world.ezra.loan_management.notification.internal.enums.NotificationStatus;
import world.ezra.loan_management.notification.internal.model.Notification;
import world.ezra.loan_management.notification.internal.repository.NotificationRepository;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationApi {
    private final NotificationRepository notificationRepository;


    @Override
    public Notification sendNotification(NotificationRequest request) {
        // todo:: send notification and create with status
        Notification notification = Notification.builder()
                .loanId(request.loanId())
                .customerId(request.customerId())
                .renderedMessage(request.message())
                .channel(request.channel())
                .status(NotificationStatus.PENDING)
                .build();
        return notificationRepository.save(notification);
    }
}
