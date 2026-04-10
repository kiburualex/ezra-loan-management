package world.ezra.loan_management.customer.internal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import world.ezra.loan_management.customer.api.CustomerApi;
import world.ezra.loan_management.customer.internal.dto.CustomerRequest;

/**
 * @author Alex Kiburu
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class CustomerController {

    private final CustomerApi customerApi;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String searchTerm,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "id") String sortBy,
                                    @RequestParam(defaultValue = "desc") String sortDirection) {
        return customerApi.findAll(searchTerm, page, size, sortBy, sortDirection);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CustomerRequest request) {
        return customerApi.create(request);
    }
}