package world.ezra.loan_management.scoring.internal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Alex Kiburu
 */
@Entity
@Table(name = "credit_scoring_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditScoringHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "decision", length = 20)
    private String decision;

    @Column(name = "calculated_at", updatable = false)
    private LocalDateTime calculatedAt;

    @Column(name = "repayment_factor", precision = 5, scale = 2)
    private BigDecimal repaymentFactor;

    @Column(name = "default_factor", precision = 5, scale = 2)
    private BigDecimal defaultFactor;

    @Column(name = "utilization_factor", precision = 5, scale = 2)
    private BigDecimal utilizationFactor;

    @Column(name = "raw_formula", columnDefinition = "TEXT")
    private String rawFormula;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
