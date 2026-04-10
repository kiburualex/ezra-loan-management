package world.ezra.loan_management.loan.internal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ezra.loan_management.common.enums.FeeType;
import world.ezra.loan_management.common.enums.TenureType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Alex Kiburu
 */
@Entity
@Table(name = "loans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
