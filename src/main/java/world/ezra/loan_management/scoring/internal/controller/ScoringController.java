package world.ezra.loan_management.scoring.internal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import world.ezra.loan_management.scoring.api.CreditScoringApi;
import world.ezra.loan_management.scoring.dto.ScoringRequest;

/**
 * @author Alex Kiburu
 */
@RestController
@RequestMapping("/api/scoring")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ScoringController {
    private final CreditScoringApi creditScoringAPI;

    @PostMapping("/customer-eligibility")
    public ResponseEntity<?> getEligibleAmount(@Valid @RequestBody ScoringRequest request) {
        return creditScoringAPI.getMaxEligibleAmount(request);
    }
}
