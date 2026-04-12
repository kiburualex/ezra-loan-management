package world.ezra.loan_management.loan.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.common.enums.LoanFeeType;
import world.ezra.loan_management.loan.internal.model.Loan;
import world.ezra.loan_management.loan.internal.model.LoanFee;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Alex Kiburu
 */
@Repository
public interface LoanFeeRepository extends JpaRepository<@NonNull LoanFee, @NonNull Long> {

    // Find all fees for a specific loan
    List<LoanFee> findByLoanId(Long loanId);

    List<LoanFee> findByLoan(Loan loan);

    // Find fees by type for a loan
    List<LoanFee> findByLoanIdAndFeeType(Long loanId, LoanFeeType feeType);

    // Find fees applied within a date range
    List<LoanFee> findByAppliedDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Get total fees by type for a loan
    @Query("SELECT COALESCE(SUM(lf.amount), 0) FROM LoanFee lf WHERE lf.loan.id = :loanId AND lf.feeType = :feeType")
    BigDecimal getTotalFeesByType(@Param("loanId") Long loanId, @Param("feeType") LoanFee.FeeType feeType);

    // Get all fees for a loan (summed by type)
    @Query("SELECT lf.feeType, COALESCE(SUM(lf.amount), 0) FROM LoanFee lf WHERE lf.loan.id = :loanId GROUP BY lf.feeType")
    List<Object[]> getFeeSummaryByLoan(@Param("loanId") Long loanId);

    // Get total fees for a loan (all types combined)
    @Query("SELECT COALESCE(SUM(lf.amount), 0) FROM LoanFee lf WHERE lf.loan.id = :loanId")
    BigDecimal getTotalFeesForLoan(@Param("loanId") Long loanId);

    // Find fees by customer (through loan relationship)
    @Query("SELECT lf FROM LoanFee lf WHERE lf.loan.customer.id = :customerId")
    List<LoanFee> findByCustomerId(@Param("customerId") Long customerId);

    // Find latest fee applied for a loan
    Optional<LoanFee> findFirstByLoanOrderByAppliedDateDesc(Loan loan);

    // Check if a loan has any fees of a specific type
    boolean existsByLoanAndFeeType(Loan loan, LoanFee.FeeType feeType);

    // Delete all fees for a loan (useful for testing or recalculation)
    void deleteByLoan(Loan loan);

    // Paginated fees for a loan
    Page<LoanFee> findByLoanId(Long loanId, Pageable pageable);

    // Find fees applied in the last X days
    @Query("SELECT lf FROM LoanFee lf WHERE lf.appliedDate >= :sinceDate")
    List<LoanFee> findFeesAppliedSince(@Param("sinceDate") LocalDateTime sinceDate);

    // Get total late fees accrued for a loan (for overdue reporting)
    @Query("SELECT COALESCE(SUM(lf.amount), 0) FROM LoanFee lf WHERE lf.loan.id = :loanId AND lf.feeType = 'LATE'")
    BigDecimal getTotalLateFees(@Param("loanId") Long loanId);

    // Get total daily fees accrued for a loan
    @Query("SELECT COALESCE(SUM(lf.amount), 0) FROM LoanFee lf WHERE lf.loan.id = :loanId AND lf.feeType = 'DAILY'")
    BigDecimal getTotalDailyFees(@Param("loanId") Long loanId);
}
