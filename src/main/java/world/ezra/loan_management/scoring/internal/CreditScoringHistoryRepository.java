package world.ezra.loan_management.scoring.internal;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import world.ezra.loan_management.scoring.internal.model.CreditScoringHistory;

/**
 * @author Alex Kiburu
 */
public interface CreditScoringHistoryRepository extends JpaRepository<@NonNull CreditScoringHistory, @NonNull Long> {

}