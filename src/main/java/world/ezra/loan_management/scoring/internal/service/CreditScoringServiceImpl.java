package world.ezra.loan_management.scoring.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import world.ezra.loan_management.common.dto.GenericResponse;
import world.ezra.loan_management.common.enums.TenureType;
import world.ezra.loan_management.common.exceptions.OperationNotPermittedException;
import world.ezra.loan_management.customer.api.CustomerApi;
import world.ezra.loan_management.customer.internal.model.Customer;
import world.ezra.loan_management.customer.internal.model.CustomerFinancialMetrics;
import world.ezra.loan_management.customer.internal.repository.CustomerFinancialMetricsRepository;
import world.ezra.loan_management.product.api.ProductApi;
import world.ezra.loan_management.product.internal.model.Product;
import world.ezra.loan_management.scoring.api.CreditScoringApi;
import world.ezra.loan_management.scoring.dto.CreditDecision;
import world.ezra.loan_management.scoring.dto.ScoringRequest;
import world.ezra.loan_management.scoring.internal.CreditScoringHistoryRepository;
import world.ezra.loan_management.scoring.internal.model.CreditScoringHistory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreditScoringServiceImpl implements CreditScoringApi {

    private final CustomerFinancialMetricsRepository metricsRepository;
    private final CreditScoringHistoryRepository scoringHistoryRepository;
    private final CustomerApi customerApi;
    private final ProductApi productApi;

    // Configurable scoring formula
    private static final Map<String, Double> WEIGHTS = Map.of(
            "repayment", 0.40,      // 40% weight
            "default", 0.30,        // 30% weight
            "utilization", 0.20,    // 20% weight
            "tenure", 0.10          // 10% weight
    );

    /**
     * Checks max eligibility for a customer (Discovery/Pre-qualification).
     */
    public ResponseEntity<?> getMaxEligibleAmount(ScoringRequest request) {
        // Check if customer exists
        Customer customer = customerApi.findById(request.customerId())
                .orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + request.customerId()));

        // Check if product exists
        Product product = productApi.findById(request.productId())
                .orElseThrow(() -> new NoSuchElementException("Product not found with ID: " + request.productId()));

        // Check if product is active
        if (!product.getActive()) {
            throw new OperationNotPermittedException("Product '" + product.getName() + "' is not active for loans");
        }

        log.info("Checking max eligibility for customer: {}", customer.getId());
        // Pass null for requestedAmount to indicate a discovery check
        CreditDecision decision = calculateScoreInternal(customer, product, null);
        Map<String, Object> eligibleMap = Map.of("amount", decision.eligibleAmount());
        GenericResponse response = GenericResponse.builder()
                .status("00")
                .message("Eligible request processed")
                .data(eligibleMap)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Standard loan application scoring.
     */
    @Override
    public CreditDecision calculateScore(Customer customer, Product product, BigDecimal requestedAmount) {
        log.info("Calculating credit score for loan application: customer={}, amount={}",
                customer.getId(), requestedAmount);
        return calculateScoreInternal(customer, product, requestedAmount);
    }


    /**
     * Calculate credit score for a customer before loan approval
     * Returns score between 300-850
     */
    private CreditDecision calculateScoreInternal(Customer customer, Product product, BigDecimal requestedAmount) {
        log.info("Calculating credit score for customer: {}", customer.getId());

        // Get metrics (they should exist from customer creation)
        var metrics = metricsRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    // Create default metrics if not found (shouldn't happen)
                    log.warn("No metrics found for customer {}, creating default", customer.getId());
                    CustomerFinancialMetrics defaultMetrics = CustomerFinancialMetrics.builder()
                            .customerId(customer.getId())
                            .totalLoansTaken(0)
                            .totalAmountBorrowed(BigDecimal.ZERO)
                            .totalAmountRepaid(BigDecimal.ZERO)
                            .onTimeRepaymentRate(new BigDecimal("1.0000"))
                            .numberOfDefaults(0)
                            .totalDaysLate(0)
                            .averageDaysLate(BigDecimal.ZERO)
                            .build();
                    return metricsRepository.save(defaultMetrics);
                });

        // Calculate individual factors (0-100 scale)
        double repaymentFactor = calculateRepaymentFactor(metrics);
        double defaultFactor = calculateDefaultFactor(metrics);
        // If no requested amount (eligibility check), we assume neutral utilization (100.0)
        // so the user sees their potential maximum limit.
        double utilizationFactor = (requestedAmount == null)
                ? 100.0
                : calculateUtilizationFactor(requestedAmount, customer);
        double tenureFactor = calculateTenureFactor(product);

        // Calculate weighted score (0-100)
        double weightedScore = (repaymentFactor * WEIGHTS.get("repayment")) +
                (defaultFactor * WEIGHTS.get("default")) +
                (utilizationFactor * WEIGHTS.get("utilization")) +
                (tenureFactor * WEIGHTS.get("tenure"));

        // Convert to 300-850 scale
        int finalScore = 300 + (int) Math.round(weightedScore * 5.5);
        finalScore = Math.min(finalScore, 850);

        // Calculate eligible amount
        BigDecimal eligibleAmount = calculateEligibleAmount(finalScore, customer);

        // Determine decision based on score AND eligibility
        String decision;
        String notes;

        if (finalScore >= 650) {
            decision = "APPROVE";
            notes = String.format("Approved. Eligible for up to %s", eligibleAmount);
            if (requestedAmount != null && requestedAmount.compareTo(eligibleAmount) > 0) {
                notes = String.format("Requested amount exceeds eligibility. Max allowed: %s", eligibleAmount);
            }
        } else if (finalScore >= 500) {
            decision = "REVIEW";
            notes = "Borderline score, requires manual review";
        } else {
            decision = "REJECT";
            notes = "Poor credit score, loan rejected";
            eligibleAmount = BigDecimal.ZERO;
        }

        // Save scoring history (Consider adding eligibleAmount to history entity too)
        CreditScoringHistory history = CreditScoringHistory.builder()
                .customerId(customer.getId())
                .score(finalScore)
                .decision(decision)
                .repaymentFactor(BigDecimal.valueOf(repaymentFactor).setScale(2, RoundingMode.HALF_UP))
                .defaultFactor(BigDecimal.valueOf(defaultFactor).setScale(2, RoundingMode.HALF_UP))
                .utilizationFactor(BigDecimal.valueOf(utilizationFactor).setScale(2, RoundingMode.HALF_UP))
                .rawFormula(generateFormula())
                .notes(notes)
                .build();

        scoringHistoryRepository.save(history);

        return new CreditDecision(finalScore, decision, notes, history, eligibleAmount);
    }

    /**
     * Factor 1: Repayment history (0-100)
     * Higher is better
     */
    private double calculateRepaymentFactor(CustomerFinancialMetrics metrics) {
        if (metrics == null || metrics.getTotalLoansTaken() == 0) {
            return 100.0; // No history = perfect score
        }

        BigDecimal rate = metrics.getOnTimeRepaymentRate();
        // Convert from 0-1 to 0-100
        return rate.doubleValue() * 100;
    }

    /**
     * Factor 2: Default history (0-100)
     * Lower defaults = higher score
     */
    private double calculateDefaultFactor(CustomerFinancialMetrics metrics) {
        if (metrics == null || metrics.getNumberOfDefaults() == 0) {
            return 100.0; // No defaults = perfect score
        }

        int defaults = metrics.getNumberOfDefaults();
        int totalLoans = metrics.getTotalLoansTaken();

        double defaultRate = (double) defaults / totalLoans;

        // Convert default rate to score (0 defaults = 100, 100% defaults = 0)
        return Math.max(0, 100 - (defaultRate * 100));
    }

    /**
     * Factor 3: Utilization (how much of limit they're using)
     * Lower utilization = higher score
     */
    private double calculateUtilizationFactor(BigDecimal requestedAmount,
                                              Customer customer) {
        BigDecimal currentLimit = customer.getCurrentLoanLimit();

        if (currentLimit == null || currentLimit.compareTo(BigDecimal.ZERO) == 0) {
            return 50.0;
        }

        BigDecimal utilization = requestedAmount.divide(currentLimit, 4, RoundingMode.HALF_UP);
        double score = Math.max(0, 100 - (utilization.doubleValue() * 100));

        return Math.min(score, 100);
    }

    /**
     * Factor 4: Tenure appropriateness
     * Longer tenures for large loans = higher score
     */
    private double calculateTenureFactor(Product product) {
        if (product.getTenureType() == TenureType.MONTHS) {
            // Longer tenure for larger loans is better
            if (product.getTenureValue() <= 6) return 80.0;
            if (product.getTenureValue() <= 12) return 90.0;
            return 100.0;
        } else { // DAYS
            if (product.getTenureValue() <= 30) return 100.0;
            if (product.getTenureValue() <= 60) return 80.0;
            return 60.0;
        }
    }

    private String generateFormula() {
        return String.format("Score = 300 + (repayment_factor * %.2f + default_factor * %.2f + utilization_factor * %.2f + tenure_factor * %.2f) * 5.5",
                WEIGHTS.get("repayment"), WEIGHTS.get("default"),
                WEIGHTS.get("utilization"), WEIGHTS.get("tenure"));
    }

    /**
     * Calculates the eligible amount based on the final score and the customer's limit.
     */
    private BigDecimal calculateEligibleAmount(int finalScore, Customer customer) {
        BigDecimal limit = customer.getCurrentLoanLimit();
        if (limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        double multiplier;
        // Logic: The better the score, the higher the percentage of the limit allowed
        if (finalScore >= 800) multiplier = 1.0;      // 100% of limit
        else if (finalScore >= 700) multiplier = 0.8; // 80% of limit
        else if (finalScore >= 650) multiplier = 0.6; // 60% of limit
        else if (finalScore >= 500) multiplier = 0.3; // 30% for manual review cases
        else multiplier = 0.0;                        // Rejected

        return limit.multiply(BigDecimal.valueOf(multiplier))
                .setScale(2, RoundingMode.HALF_DOWN);
    }
}
