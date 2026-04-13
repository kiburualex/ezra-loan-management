package world.ezra.loan_management.repayment.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import world.ezra.loan_management.repayment.internal.model.Repayment;

/**
 * @author Alex Kiburu
 */
public interface RepaymentRepository extends JpaRepository<@NonNull Repayment, @NonNull Long> {

}
