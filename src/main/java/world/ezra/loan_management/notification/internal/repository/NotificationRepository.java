package world.ezra.loan_management.notification.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.notification.internal.model.Notification;

/**
 * @author Alex Kiburu
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}