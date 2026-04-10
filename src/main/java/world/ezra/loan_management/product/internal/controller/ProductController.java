package world.ezra.loan_management.product.internal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import world.ezra.loan_management.product.api.ProductApi;
import world.ezra.loan_management.product.internal.dto.ProductRequest;

/**
 * @author Alex Kiburu
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {
    private final ProductApi productApi;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String searchTerm,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "id") String sortBy,
                                    @RequestParam(defaultValue = "desc") String sortDirection) {
        return productApi.findAll(searchTerm, page, size, sortBy, sortDirection);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ProductRequest request) {
        return productApi.create(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody ProductRequest request) {
        return productApi.update(id, request);
    }
}
