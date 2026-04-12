package world.ezra.loan_management.loan.internal.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import world.ezra.loan_management.common.enums.BillingType;
import world.ezra.loan_management.common.enums.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Alex Kiburu
 */
@Getter
@Setter
@Builder
public class LoanResponse{
    private Long id;
    private Long customerId;
    private String customerName;
    private Long productId;
    private String productName;
    private BigDecimal principalAmount;
    private BigDecimal disbursedAmount;
    private BigDecimal totalRepayableAmount;
    private LocalDateTime originationDate;
    private Boolean isInstallmentBased;
    private Integer numberOfInstallments;
    private BillingType billingType;
    private Integer consolidatedDueDay;
    private LoanStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
