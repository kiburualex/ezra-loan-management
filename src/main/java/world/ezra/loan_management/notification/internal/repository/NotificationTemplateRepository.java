package world.ezra.loan_management.notification.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import world.ezra.loan_management.common.enums.PreferredChannel;
import world.ezra.loan_management.notification.internal.model.NotificationTemplate;

import java.util.Optional;

/**
 * @author Alex Kiburu
 */
public interface NotificationTemplateRepository extends JpaRepository<@NonNull NotificationTemplate, @NonNull Long> {

    Optional<NotificationTemplate> findFirstByEventTypeAndChannel(String eventType,
                                                             PreferredChannel channel);

    Optional<NotificationTemplate> findFirstByProductIdAndEventType(Long productId, String eventType);
}
