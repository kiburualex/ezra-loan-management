package world.ezra.loan_management.scoring.dto;

import world.ezra.loan_management.scoring.internal.model.CreditScoringHistory;

import java.math.BigDecimal;

/**
 * @author Alex Kiburu
 */
public record CreditDecision(
        int score,
        String decision,
        String notes,
        CreditScoringHistory history,
        BigDecimal eligibleAmount
) {
}
