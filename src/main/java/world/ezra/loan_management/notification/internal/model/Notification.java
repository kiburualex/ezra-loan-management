package world.ezra.loan_management.notification.internal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import world.ezra.loan_management.common.enums.PreferredChannel;
import world.ezra.loan_management.notification.internal.enums.NotificationStatus;

import java.time.LocalDateTime;

/**
 * @author Alex Kiburu
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "loan_id")
    private Long loanId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private PreferredChannel channel;

    @Column(name = "rendered_message", nullable = false, columnDefinition = "TEXT")
    private String renderedMessage;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private NotificationStatus status;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
