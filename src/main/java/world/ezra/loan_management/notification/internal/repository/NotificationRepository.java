package world.ezra.loan_management.notification.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import world.ezra.loan_management.notification.internal.model.Notification;

/**
 * @author Alex Kiburu
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}