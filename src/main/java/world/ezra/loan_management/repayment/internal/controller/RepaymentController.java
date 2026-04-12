package world.ezra.loan_management.repayment.internal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import world.ezra.loan_management.repayment.api.RepaymentApi;
import world.ezra.loan_management.repayment.internal.dto.RepaymentRequest;

/**
 * @author Alex Kiburu
 */
@RestController
@RequestMapping("/api/repayments")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class RepaymentController {
    private final RepaymentApi repaymentApi;

    @PostMapping
    public ResponseEntity<?> repay(@Valid @RequestBody RepaymentRequest request) {
        return repaymentApi.repay(request);
    }
}
