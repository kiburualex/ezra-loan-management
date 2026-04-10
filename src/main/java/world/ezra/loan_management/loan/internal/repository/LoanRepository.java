package world.ezra.loan_management.loan.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.loan.internal.model.Loan;

/**
 * @author Alex Kiburu
 */
@Repository
public interface LoanRepository extends JpaRepository<@NonNull Loan, @NonNull Long> {
}
