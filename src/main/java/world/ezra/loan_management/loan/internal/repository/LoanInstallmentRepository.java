package world.ezra.loan_management.loan.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import world.ezra.loan_management.loan.internal.model.Loan;
import world.ezra.loan_management.loan.internal.model.LoanInstallment;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Alex Kiburu
 */
@Repository
public interface LoanInstallmentRepository extends JpaRepository<@NonNull LoanInstallment, @NonNull Long> {
    List<LoanInstallment> findByLoanIdOrderByInstallmentNumberAsc(Long loanId);

    // Check if loan has any overdue installments
    @Query("SELECT COUNT(li) > 0 FROM LoanInstallment li WHERE li.loan = :loan AND li.dueDate < :currentDate AND li.status = 'PENDING'")
    boolean hasOverdueInstallments(@Param("loan") Loan loan, @Param("currentDate") LocalDate currentDate);

    // Update installment status to OVERDUE
    @Modifying
    @Transactional
    @Query("UPDATE LoanInstallment li SET li.status = 'OVERDUE' WHERE li.dueDate < :currentDate AND li.status = 'PENDING'")
    int updateOverdueStatus(@Param("currentDate") LocalDate currentDate);
}
