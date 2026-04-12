package world.ezra.loan_management.customer.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import world.ezra.loan_management.customer.internal.model.CustomerFinancialMetrics;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Alex Kiburu
 */
@Repository
public interface CustomerFinancialMetricsRepository extends JpaRepository<@NonNull CustomerFinancialMetrics, @NonNull Long> {

    Optional<CustomerFinancialMetrics> findByCustomerId(Long customerId);

    // Update metrics after loan creation
    @Modifying
    @Transactional
    @Query("UPDATE CustomerFinancialMetrics cfm SET " +
            "cfm.totalLoansTaken = cfm.totalLoansTaken + 1, " +
            "cfm.totalAmountBorrowed = cfm.totalAmountBorrowed + :amount, " +
            "cfm.lastLoanDate = CURRENT_DATE " +
            "WHERE cfm.customer = :customerId")
    void updateForNewLoan(@Param("customerId") Long customerId, @Param("amount") BigDecimal amount);

    // Update metrics after repayment
    @Modifying
    @Transactional
    @Query("UPDATE CustomerFinancialMetrics cfm SET " +
            "cfm.totalAmountRepaid = cfm.totalAmountRepaid + :amount, " +
            "cfm.lastRepaymentDate = CURRENT_DATE " +
            "WHERE cfm.customer = :customerId")
    void updateForRepayment(@Param("customerId") Long customerId, @Param("amount") BigDecimal amount);

    // Update on-time repayment rate
    @Modifying
    @Transactional
    @Query("UPDATE CustomerFinancialMetrics cfm SET " +
            "cfm.onTimeRepaymentRate = :rate " +
            "WHERE cfm.customer = :customerId")
    void updateRepaymentRate(@Param("customerId") Long customerId, @Param("rate") BigDecimal rate);

    // Update default metrics
    @Modifying
    @Transactional
    @Query("UPDATE CustomerFinancialMetrics cfm SET " +
            "cfm.numberOfDefaults = cfm.numberOfDefaults + 1, " +
            "cfm.totalDaysLate = cfm.totalDaysLate + :daysLate, " +
            "cfm.averageDaysLate = CAST(cfm.totalDaysLate AS double) / cfm.numberOfDefaults " +
            "WHERE cfm.customer = :customerId")
    void updateForDefault(@Param("customerId") Long customerId, @Param("daysLate") Integer daysLate);

    // Initialize metrics for new customer
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO customer_financial_metrics (customer_id, total_loans_taken, total_amount_borrowed, " +
            "total_amount_repaid, on_time_repayment_rate, number_of_defaults, total_days_late, average_days_late) " +
            "VALUES (:customerId, 0, 0, 0, 1.0000, 0, 0, 0)", nativeQuery = true)
    void initializeMetrics(@Param("customerId") Long customerId);
}
