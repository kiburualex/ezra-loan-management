package world.ezra.loan_management.scoring.internal;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.scoring.internal.model.CreditScoringHistory;

import java.util.List;
import java.util.Optional;

/**
 * @author Alex Kiburu
 */

@Repository
public interface CreditScoringHistoryRepository extends JpaRepository<@NonNull CreditScoringHistory, @NonNull Long> {

    // Find all scoring records for a customer, most recent first
    List<CreditScoringHistory> findByCustomerIdOrderByCalculatedAtDesc(Long customerId);

    // Find most recent scoring record for a customer
    Optional<CreditScoringHistory> findFirstByCustomerIdOrderByCalculatedAtDesc(Long customerId);

    // Paginated scoring history for a customer
    Page<@NonNull CreditScoringHistory> findByCustomerId(Long customerId, Pageable pageable);

    // Find all approvals/rejections for a customer
    List<CreditScoringHistory> findByCustomerIdAndDecision(Long customerId, String decision);

    // Get average score for a customer over time
    @Query("SELECT AVG(csh.score) FROM CreditScoringHistory csh WHERE csh.customerId = :customerId")
    Double getAverageScoreForCustomer(@Param("customerId") Long customerId);

    // Count how many times customer was rejected
    @Query("SELECT COUNT(csh) FROM CreditScoringHistory csh WHERE csh.customerId = :customerId AND csh.decision = 'REJECT'")
    Long countRejectionsByCustomer(@Param("customerId") Long customerId);
}