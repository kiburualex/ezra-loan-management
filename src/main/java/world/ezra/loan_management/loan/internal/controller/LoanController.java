package world.ezra.loan_management.loan.internal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import world.ezra.loan_management.loan.api.LoanApi;
import world.ezra.loan_management.loan.internal.dto.LoanRequest;
import world.ezra.loan_management.scoring.dto.ScoringRequest;

/**
 * @author Alex Kiburu
 */
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class LoanController {
    private final LoanApi loanApi;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String searchTerm,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "id") String sortBy,
                                    @RequestParam(defaultValue = "desc") String sortDirection) {
        return loanApi.findAll(searchTerm, page, size, sortBy, sortDirection);
    }

    @PostMapping("/new")
    public ResponseEntity<?> newLoanApplication(@Valid @RequestBody LoanRequest request) {
        return loanApi.newLoanApplication(request);
    }
}
