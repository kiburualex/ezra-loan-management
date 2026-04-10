package world.ezra.loan_management.loan.internal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import world.ezra.loan_management.loan.api.LoanApi;

/**
 * @author Alex Kiburu
 */
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class LoanController {
    private final LoanApi loanApi;
}
