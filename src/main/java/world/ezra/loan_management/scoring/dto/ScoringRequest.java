package world.ezra.loan_management.scoring.dto;

import jakarta.validation.constraints.NotNull;

/**
 * @author Alex Kiburu
 */
public record ScoringRequest(
        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotNull(message = "Product ID is required")
        Long productId
) {
}
