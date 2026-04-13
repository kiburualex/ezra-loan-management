package world.ezra.loan_management.loan.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.loan.internal.model.Loan;
import world.ezra.loan_management.loan.internal.model.LoanFee;
import world.ezra.loan_management.product.internal.model.Product;

import java.util.List;

/**
 * @author Alex Kiburu
 */
@Repository
public interface LoanRepository extends JpaRepository<@NonNull Loan, @NonNull Long> {
    // find loans that are overdue by expected payment date
    // find loans that have status as overdue for processing
}
