package world.ezra.loan_management.notification.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.notification.internal.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Alex Kiburu
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByCustomerIdAndStatus(Long customerId, Notification.NotificationStatus status);

    List<Notification> findByStatus(Notification.NotificationStatus status);

    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' AND n.createdAt < :timeout")
    List<Notification> findPendingNotificationsOlderThan(@Param("timeout") LocalDateTime timeout);
}