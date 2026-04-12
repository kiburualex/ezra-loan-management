package world.ezra.loan_management.notification.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.common.enums.PreferredChannel;
import world.ezra.loan_management.notification.internal.model.NotificationTemplate;

import java.util.List;
import java.util.Optional;

/**
 * @author Alex Kiburu
 */
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByEventTypeAndChannel(String eventType,
                                                             PreferredChannel channel);

    Optional<NotificationTemplate> findFirstByProductIdAndEventType(Long productId, String eventType);
}
