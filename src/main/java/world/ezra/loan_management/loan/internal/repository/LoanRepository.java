package world.ezra.loan_management.loan.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import world.ezra.loan_management.loan.internal.model.Loan;

/**
 * @author Alex Kiburu
 */
public interface LoanRepository extends JpaRepository<@NonNull Loan, @NonNull Long> {
    // find loans that are overdue by expected payment date
    // find loans that have status as overdue for processing
}
