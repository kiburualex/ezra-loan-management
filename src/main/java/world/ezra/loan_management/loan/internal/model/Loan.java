package world.ezra.loan_management.loan.internal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ezra.loan_management.common.enums.BillingType;
import world.ezra.loan_management.common.enums.LoanStatus;
import world.ezra.loan_management.customer.internal.model.Customer;
import world.ezra.loan_management.product.internal.model.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "principal_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "disbursed_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal disbursedAmount;

    @Column(name = "total_repayable_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalRepayableAmount;

    @Column(name = "credit_score")
    private Integer creditScore;

    @Column(name = "origination_date", nullable = false, updatable = false)
    private LocalDateTime originationDate;

    @Column(name = "is_installment_based", nullable = false)
    private Boolean isInstallmentBased;

    @Column(name = "number_of_installments")
    private Integer numberOfInstallments;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", nullable = false, length = 20)
    private BillingType billingType;

    @Column(name = "consolidated_due_day")
    private Integer consolidatedDueDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoanStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LoanFee> loanFees;

    @PrePersist
    protected void onCreate() {
        if (originationDate == null)
            originationDate = LocalDateTime.now();

        if (isInstallmentBased == null)
            isInstallmentBased = false;

        if (billingType == null)
            billingType = BillingType.INDIVIDUAL;

        if (status == null)
            status = LoanStatus.OPEN;

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
