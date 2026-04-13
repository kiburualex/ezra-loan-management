package world.ezra.loan_management.repayment.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.repayment.internal.model.Repayment;

/**
 * @author Alex Kiburu
 */
@Repository
public interface RepaymentRepository extends JpaRepository<@NonNull Repayment, @NonNull Long> {


}
