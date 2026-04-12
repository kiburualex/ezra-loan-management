package world.ezra.loan_management.customer.api;

import org.springframework.http.ResponseEntity;
import world.ezra.loan_management.customer.internal.dto.CustomerRequest;
import world.ezra.loan_management.customer.internal.model.Customer;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Alex Kiburu
 */
public interface CustomerApi {
    ResponseEntity<?> findAll(String searchTerm, int page, int size, String sortBy, String sortDirection);
    ResponseEntity<?> create(CustomerRequest request);
    Optional<Customer> findById(Long id);
    void updateCustomerMetrics(Long customerId, BigDecimal amountRepaid);
}
