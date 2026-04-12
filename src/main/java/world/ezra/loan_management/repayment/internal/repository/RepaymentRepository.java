package world.ezra.loan_management.repayment.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.repayment.internal.model.Repayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Alex Kiburu
 */
@Repository
public interface RepaymentRepository extends JpaRepository<@NonNull Repayment, @NonNull Long> {

    // Find all repayments for a loan
    List<@NonNull Repayment> findByLoanId(Long loanId);

    Page<@NonNull Repayment> findByLoanId(Long loanId, Pageable pageable);

    // Find repayments for a specific installment
    List<Repayment> findByInstallmentId(Long installmentId);

    // Get total repaid amount for a loan
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Repayment r WHERE r.loan.id = :loanId")
    BigDecimal getTotalRepaidByLoan(@Param("loanId") Long loanId);

    // Get total repaid amount for a specific installment
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Repayment r WHERE r.installment.id = :installmentId")
    BigDecimal getTotalRepaidByInstallment(@Param("installmentId") Long installmentId);

    // Find repayments within date range
    List<Repayment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find repayments by customer (through loan)
    @Query("SELECT r FROM Repayment r WHERE r.loan.customer.id = :customerId")
    List<Repayment> findByCustomerId(@Param("customerId") Long customerId);

    // Get total repayments for a customer
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Repayment r WHERE r.loan.customer.id = :customerId")
    BigDecimal getTotalRepaidByCustomer(@Param("customerId") Long customerId);
}
