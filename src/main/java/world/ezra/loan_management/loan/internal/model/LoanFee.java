package world.ezra.loan_management.loan.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import world.ezra.loan_management.common.enums.LoanFeeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Alex Kiburu
 */
@Entity
@Table(name = "loan_fees")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false, length = 20)
    private LoanFeeType feeType;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "applied_date", nullable = false)
    private LocalDateTime appliedDate;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    /**
     * Enum for fee types
     */
    public enum FeeType {
        SERVICE,   // Service fee applied at loan origination
        DAILY,     // Daily fee accrued on overdue loans
        LATE       // Late fee applied after due date
    }

    @PrePersist
    protected void onCreate() {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        if (appliedDate == null) {
            appliedDate = LocalDateTime.now();
        }
    }
}
