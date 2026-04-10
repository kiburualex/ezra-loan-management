package world.ezra.loan_management.product.api;

import org.springframework.http.ResponseEntity;
import world.ezra.loan_management.product.internal.dto.ProductRequest;

/**
 * @author Alex Kiburu
 */
public interface ProductApi {
    ResponseEntity<?> findAll(String searchTerm, int page, int size, String sortBy, String sortDirection);
    ResponseEntity<?> create(ProductRequest request);
    ResponseEntity<?> update(Long id, ProductRequest request);
}
