package world.ezra.loan_management.scoring.internal;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.scoring.internal.model.CreditScoringHistory;

/**
 * @author Alex Kiburu
 */

@Repository
public interface CreditScoringHistoryRepository extends JpaRepository<@NonNull CreditScoringHistory, @NonNull Long> {

}