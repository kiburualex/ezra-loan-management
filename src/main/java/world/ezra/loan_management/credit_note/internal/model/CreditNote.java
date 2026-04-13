package world.ezra.loan_management.credit_note.internal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ezra.loan_management.credit_note.internal.enums.CreditNoteReason;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Alex Kiburu
 */
@Entity
@Table(name = "credit_notes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private CreditNoteReason reason;

    @Column(name = "notes")
    private String notes;

    @Column(name = "used", nullable = false)
    private Boolean used = false;

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
