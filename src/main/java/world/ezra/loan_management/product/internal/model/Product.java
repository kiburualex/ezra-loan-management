package world.ezra.loan_management.product.internal.model;

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
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenure_type", length = 20, nullable = false)
    private TenureType tenureType;

    @Column(name = "tenure_value", nullable = false)
    private Integer tenureValue;

    // todo:: refactor
    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_fee_type", length = 20)
    private FeeType serviceFeeType;

    // todo:: review the scale
    @Column(name = "service_fee_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal serviceFeeValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "daily_fee_type", length = 20)
    private FeeType dailyFeeType;

    @Column(name = "daily_fee_value", precision = 15, scale = 2)
    private BigDecimal dailyFeeValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "late_fee_type", length = 20)
    private FeeType lateFeeType;

    @Column(name = "late_fee_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal lateFeeValue;

    @Column(name = "days_after_due_for_late_fee", nullable = false)
    private Integer daysAfterDueForLateFee;

    @Column(name = "days_after_due_for_daily_fee")
    private Integer daysAfterDueForDailyFee;

    @Column(name = "min_loan_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal minLoanAmount;

    @Column(name = "max_loan_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxLoanAmount;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (active == null) {
            active = true;
        }
        if (minLoanAmount == null) {
            minLoanAmount = new BigDecimal("1000.00");
        }
        if (maxLoanAmount == null) {
            maxLoanAmount = new BigDecimal("50000.00");
        }
        if (daysAfterDueForLateFee == null) {
            daysAfterDueForLateFee = 3;
        }
        if (serviceFeeValue == null) {
            serviceFeeValue = BigDecimal.ZERO;
        }
        if (lateFeeValue == null) {
            lateFeeValue = BigDecimal.ZERO;
        }

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
