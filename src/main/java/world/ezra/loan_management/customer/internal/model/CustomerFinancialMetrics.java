package world.ezra.loan_management.customer.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Alex Kiburu
 */
@Entity
@Table(name = "customer_financial_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFinancialMetrics {

    @Id
    private Long customerId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "total_loans_taken")
    private Integer totalLoansTaken;

    @Column(name = "total_amount_borrowed", precision = 15, scale = 2)
    private BigDecimal totalAmountBorrowed;

    @Column(name = "total_amount_repaid", precision = 15, scale = 2)
    private BigDecimal totalAmountRepaid;

    @Column(name = "on_time_repayment_rate", precision = 5, scale = 4)
    private BigDecimal onTimeRepaymentRate;

    @Column(name = "number_of_defaults")
    private Integer numberOfDefaults;

    @Column(name = "total_days_late")
    private Integer totalDaysLate;

    @Column(name = "average_days_late", precision = 8, scale = 2)
    private BigDecimal averageDaysLate;

    @Column(name = "last_loan_date")
    private LocalDate lastLoanDate;

    @Column(name = "last_repayment_date")
    private LocalDate lastRepaymentDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (totalLoansTaken == null) totalLoansTaken = 0;
        if (totalAmountBorrowed == null) totalAmountBorrowed = BigDecimal.ZERO;
        if (totalAmountRepaid == null) totalAmountRepaid = BigDecimal.ZERO;
        if (onTimeRepaymentRate == null) onTimeRepaymentRate = new BigDecimal("1.0000");
        if (numberOfDefaults == null) numberOfDefaults = 0;
        if (totalDaysLate == null) totalDaysLate = 0;
        if (averageDaysLate == null) averageDaysLate = BigDecimal.ZERO;

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}