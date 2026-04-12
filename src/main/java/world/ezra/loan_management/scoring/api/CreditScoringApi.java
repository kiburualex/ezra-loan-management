package world.ezra.loan_management.scoring.api;

import org.springframework.http.ResponseEntity;
import world.ezra.loan_management.customer.internal.model.Customer;
import world.ezra.loan_management.product.internal.model.Product;
import world.ezra.loan_management.scoring.dto.CreditDecision;
import world.ezra.loan_management.scoring.dto.ScoringRequest;

import java.math.BigDecimal;

/**
 * @author Alex Kiburu
 */
public interface CreditScoringApi {
    CreditDecision calculateScore(Customer customer, Product product, BigDecimal requestedAmount);
    ResponseEntity<?> getMaxEligibleAmount(ScoringRequest request);
}
