package world.ezra.loan_management.loan.api;

import org.springframework.http.ResponseEntity;
import world.ezra.loan_management.loan.internal.dto.LoanRequest;

/**
 * @author Alex Kiburu
 */
public interface LoanApi {
    ResponseEntity<?> findAll(String searchTerm, int page, int size, String sortBy, String sortDirection);
    ResponseEntity<?> newLoanApplication(LoanRequest request);
}
