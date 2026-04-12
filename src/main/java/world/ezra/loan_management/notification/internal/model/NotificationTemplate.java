package world.ezra.loan_management.notification.internal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import world.ezra.loan_management.common.enums.PreferredChannel;

import java.time.LocalDateTime;

/**
 * @author Alex Kiburu
 */
@Entity
@Table(name = "notification_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false, unique = true, length = 100)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private PreferredChannel channel;

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "product_id")
    private Long productId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}