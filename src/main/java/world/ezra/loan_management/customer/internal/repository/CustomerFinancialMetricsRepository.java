package world.ezra.loan_management.customer.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import world.ezra.loan_management.customer.internal.model.CustomerFinancialMetrics;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Alex Kiburu
 */
public interface CustomerFinancialMetricsRepository extends JpaRepository<@NonNull CustomerFinancialMetrics, @NonNull Long> {

    Optional<CustomerFinancialMetrics> findByCustomerId(Long customerId);

    // Update on-time repayment rate
    @Modifying
    @Transactional
    @Query("UPDATE CustomerFinancialMetrics cfm SET " +
            "cfm.onTimeRepaymentRate = :rate " +
            "WHERE cfm.customer = :customerId")
    void updateRepaymentRate(@Param("customerId") Long customerId, @Param("rate") BigDecimal rate);
}
