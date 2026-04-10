package world.ezra.loan_management.product.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import world.ezra.loan_management.common.dto.GenericResponse;
import world.ezra.loan_management.common.dto.PaginatedResponse;
import world.ezra.loan_management.common.exceptions.OperationNotPermittedException;
import world.ezra.loan_management.product.api.ProductApi;
import world.ezra.loan_management.product.internal.dto.ProductRequest;
import world.ezra.loan_management.product.internal.dto.ProductResponse;
import world.ezra.loan_management.product.internal.mappers.ProductMapper;
import world.ezra.loan_management.product.internal.model.Product;
import world.ezra.loan_management.product.internal.repository.ProductRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductApi {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ResponseEntity<?> findAll(String searchTerm, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // If search term is null or empty, return all customers
        if (StringUtils.isEmpty(searchTerm)) {
            Page<@NonNull Product> customerPage = productRepository.findAll(pageable);
            return ResponseEntity.ok(new PaginatedResponse<>(customerPage));
        }

        Page<@NonNull Product> customerPage = productRepository.searchByAllFields(searchTerm.trim(), pageable);
        return ResponseEntity.ok(new PaginatedResponse<>(customerPage));
    }

    @Override
    public ResponseEntity<?> create(ProductRequest request) {
        try {
            log.info("Creating new product with name: {}, {}", request.name(), request);

            // check for duplicate name
            if (productRepository.findFirstByName(request.name()).isPresent()) {
                throw new NoSuchElementException("Product with name '" + request.name() + "' already exists");
            }

            // map request to entity
            Product product = productMapper.toEntity(request);

            // save to database
            Product savedProduct = productRepository.save(product);
            ProductResponse productResponse = productMapper.toResponse(savedProduct);
            GenericResponse response = GenericResponse.builder()
                    .status("00")
                    .message("Product created successfully")
                    .data(productResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new OperationNotPermittedException("Error creating product:: " + e.getMessage());

        }
    }

    @Override
    public ResponseEntity<?> update(Long id, ProductRequest request) {
        try {
            log.info("Updating product with ID: {}, {}", id, request);

            // Check if product exists
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Product not found with ID: " + id));

            // Check for duplicate name - but exclude the current product being updated
            Optional<Product> productWithSameName = productRepository.findFirstByName(request.name());
            if (productWithSameName.isPresent() && !productWithSameName.get().getId().equals(id)) {
                throw new IllegalArgumentException("Product with name '" + request.name() + "' already exists");
            }

            // Update the existing entity with new values
            productMapper.updateEntity(existingProduct, request);

            // Save updated entity to database
            Product updatedProduct = productRepository.save(existingProduct);
            log.info("Product updated successfully with ID: {}", updatedProduct.getId());

            // Convert to response DTO
            ProductResponse productResponse = productMapper.toResponse(updatedProduct);

            // Build and return response
            GenericResponse response = GenericResponse.builder()
                    .status("00")
                    .message("Product updated successfully")
                    .data(productResponse)
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage());
            throw new OperationNotPermittedException("Error updating product: " + e.getMessage());
        }
    }
}
