package world.ezra.loan_management.customer.api;

import org.springframework.http.ResponseEntity;
import world.ezra.loan_management.customer.internal.dto.CustomerRequest;

/**
 * @author Alex Kiburu
 */
public interface CustomerApi {
    ResponseEntity<?> findAll(String searchTerm, int page, int size, String sortBy, String sortDirection);
    ResponseEntity<?> create(CustomerRequest request);
}
