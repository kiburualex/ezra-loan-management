package world.ezra.loan_management.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import world.ezra.loan_management.common.dto.GenericResponse;
import world.ezra.loan_management.common.dto.PaginatedResponse;
import world.ezra.loan_management.common.enums.FeeType;
import world.ezra.loan_management.common.enums.TenureType;
import world.ezra.loan_management.common.exceptions.OperationNotPermittedException;
import world.ezra.loan_management.product.internal.dto.ProductRequest;
import world.ezra.loan_management.product.internal.dto.ProductResponse;
import world.ezra.loan_management.product.internal.mappers.ProductMapper;
import world.ezra.loan_management.product.internal.model.Product;
import world.ezra.loan_management.product.internal.repository.ProductRepository;
import world.ezra.loan_management.product.internal.service.ProductServiceImpl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Alex Kiburu
 */
@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest validRequest;
    private Product product;
    private Product savedProduct;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        validRequest = new ProductRequest(
                "Business Loan Pro",
                "Premium business loan for established businesses",
                TenureType.MONTHS,
                12,
                new BigDecimal("12.5000"),
                FeeType.FIXED,
                new BigDecimal("1500.00"),
                FeeType.PERCENTAGE,
                new BigDecimal("0.005"),
                FeeType.FIXED,
                new BigDecimal("500.00"),
                5,
                10,
                new BigDecimal("10000.00"),
                new BigDecimal("500000.00"),
                true
        );

        product = Product.builder()
                .name("Business Loan Pro")
                .description("Premium business loan for established businesses")
                .tenureType(TenureType.MONTHS)
                .tenureValue(12)
                .interestRate(new BigDecimal("12.5000"))
                .serviceFeeType(FeeType.FIXED)
                .serviceFeeValue(new BigDecimal("1500.00"))
                .dailyFeeType(FeeType.PERCENTAGE)
                .dailyFeeValue(new BigDecimal("0.005"))
                .lateFeeType(FeeType.FIXED)
                .lateFeeValue(new BigDecimal("500.00"))
                .daysAfterDueForLateFee(5)
                .daysAfterDueForDailyFee(10)
                .minLoanAmount(new BigDecimal("10000.00"))
                .maxLoanAmount(new BigDecimal("500000.00"))
                .active(true)
                .build();

        savedProduct = Product.builder()
                .id(1L)
                .name("Business Loan Pro")
                .description("Premium business loan for established businesses")
                .tenureType(TenureType.MONTHS)
                .tenureValue(12)
                .interestRate(new BigDecimal("12.5000"))
                .serviceFeeType(FeeType.FIXED)
                .serviceFeeValue(new BigDecimal("1500.00"))
                .dailyFeeType(FeeType.PERCENTAGE)
                .dailyFeeValue(new BigDecimal("0.005"))
                .lateFeeType(FeeType.FIXED)
                .lateFeeValue(new BigDecimal("500.00"))
                .daysAfterDueForLateFee(5)
                .daysAfterDueForDailyFee(10)
                .minLoanAmount(new BigDecimal("10000.00"))
                .maxLoanAmount(new BigDecimal("500000.00"))
                .active(true)
                .build();

        productResponse = new ProductResponse(
                1L,
                "Business Loan Pro",
                "Premium business loan for established businesses",
                TenureType.MONTHS,
                12,
                new BigDecimal("12.5000"),
                FeeType.FIXED,
                new BigDecimal("1500.00"),
                FeeType.PERCENTAGE,
                new BigDecimal("0.005"),
                FeeType.FIXED,
                new BigDecimal("500.00"),
                5,
                10,
                new BigDecimal("10000.00"),
                new BigDecimal("500000.00"),
                true,
                null,
                null
        );
    }

    @Nested
    @DisplayName("Create Product Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product successfully")
        void shouldCreateProductSuccessfully() {
            when(productRepository.findFirstByName(validRequest.name())).thenReturn(Optional.empty());
            when(productMapper.toEntity(validRequest)).thenReturn(product);
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
            when(productMapper.toResponse(savedProduct)).thenReturn(productResponse);

            ResponseEntity<?> response = productService.create(validRequest);

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            GenericResponse genericResponse = (GenericResponse) response.getBody();
            assertThat(genericResponse).isNotNull();
            assertThat(genericResponse.getStatus()).isEqualTo("00");
            assertThat(genericResponse.getMessage()).isEqualTo("Product created successfully");

            verify(productRepository).findFirstByName(validRequest.name());
            verify(productMapper).toEntity(validRequest);
            verify(productRepository).save(product);
            verify(productMapper).toResponse(savedProduct);
        }

        @Test
        @DisplayName("Should throw exception when product name already exists")
        void shouldThrowExceptionWhenDuplicateNameExists() {
            when(productRepository.findFirstByName(validRequest.name())).thenReturn(Optional.of(savedProduct));

            assertThatThrownBy(() -> productService.create(validRequest))
                    .isInstanceOf(OperationNotPermittedException.class)
                    .hasMessageContaining("Product with name 'Business Loan Pro' already exists");

            verify(productRepository).findFirstByName(validRequest.name());
            verifyNoInteractions(productMapper);
            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update Product Tests")
    class UpdateProductTests {

        private final Long productId = 1L;

        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProductSuccessfully() {
            ProductRequest updateRequest = new ProductRequest(
                    "Updated Loan Pro",
                    "Updated description",
                    TenureType.MONTHS,
                    24,
                    new BigDecimal("15.5000"),
                    FeeType.FIXED,
                    new BigDecimal("2000.00"),
                    FeeType.PERCENTAGE,
                    new BigDecimal("0.007"),
                    FeeType.FIXED,
                    new BigDecimal("750.00"),
                    3,
                    7,
                    new BigDecimal("15000.00"),
                    new BigDecimal("750000.00"),
                    true
            );

            Product updatedProduct = Product.builder()
                    .id(1L)
                    .name("Updated Loan Pro")
                    .description("Updated description")
                    .tenureType(TenureType.MONTHS)
                    .tenureValue(24)
                    .interestRate(new BigDecimal("15.5000"))
                    .serviceFeeType(FeeType.FIXED)
                    .serviceFeeValue(new BigDecimal("2000.00"))
                    .dailyFeeType(FeeType.PERCENTAGE)
                    .dailyFeeValue(new BigDecimal("0.007"))
                    .lateFeeType(FeeType.FIXED)
                    .lateFeeValue(new BigDecimal("750.00"))
                    .daysAfterDueForLateFee(3)
                    .daysAfterDueForDailyFee(7)
                    .minLoanAmount(new BigDecimal("15000.00"))
                    .maxLoanAmount(new BigDecimal("750000.00"))
                    .active(true)
                    .build();

            ProductResponse updatedResponse = new ProductResponse(
                    1L,
                    "Updated Loan Pro",
                    "Updated description",
                    TenureType.MONTHS,
                    24,
                    new BigDecimal("15.5000"),
                    FeeType.FIXED,
                    new BigDecimal("2000.00"),
                    FeeType.PERCENTAGE,
                    new BigDecimal("0.007"),
                    FeeType.FIXED,
                    new BigDecimal("750.00"),
                    3,
                    7,
                    new BigDecimal("15000.00"),
                    new BigDecimal("750000.00"),
                    true,
                    null,
                    null
            );

            when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));
            when(productRepository.findFirstByName(updateRequest.name())).thenReturn(Optional.empty());
            when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
            when(productMapper.toResponse(updatedProduct)).thenReturn(updatedResponse);

            ResponseEntity<?> response = productService.update(productId, updateRequest);

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            GenericResponse genericResponse = (GenericResponse) response.getBody();
            assertThat(genericResponse).isNotNull();
            assertThat(genericResponse.getMessage()).isEqualTo("Product updated successfully");

            verify(productRepository).findById(productId);
            verify(productRepository).findFirstByName(updateRequest.name());
            verify(productMapper).updateEntity(savedProduct, updateRequest);
            verify(productRepository).save(savedProduct);
        }

        @Test
        @DisplayName("Should throw exception when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            Long nonExistentId = 999L;
            when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.update(nonExistentId, validRequest))
                    .isInstanceOf(OperationNotPermittedException.class)
                    .hasMessageContaining("Product not found with ID: " + nonExistentId);

            verify(productRepository).findById(nonExistentId);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("Should throw exception when updating to existing product name")
        void shouldThrowExceptionWhenUpdatingToDuplicateName() {
            Product anotherProduct = Product.builder()
                    .id(2L)
                    .name("Existing Name")
                    .build();

            ProductRequest updateRequest = new ProductRequest(
                    "Existing Name",
                    "Description",
                    TenureType.MONTHS,
                    12,
                    new BigDecimal("12.5000"),
                    FeeType.FIXED,
                    new BigDecimal("1500.00"),
                    FeeType.PERCENTAGE,
                    new BigDecimal("0.005"),
                    FeeType.FIXED,
                    new BigDecimal("500.00"),
                    5,
                    10,
                    new BigDecimal("10000.00"),
                    new BigDecimal("500000.00"),
                    true
            );

            when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));
            when(productRepository.findFirstByName(updateRequest.name())).thenReturn(Optional.of(anotherProduct));

            assertThatThrownBy(() -> productService.update(productId, updateRequest))
                    .isInstanceOf(OperationNotPermittedException.class)
                    .hasMessageContaining("Product with name 'Existing Name' already exists");
        }

        @Test
        @DisplayName("Should allow update when name remains the same")
        void shouldAllowUpdateWhenNameUnchanged() {
            ProductRequest updateRequest = new ProductRequest(
                    "Business Loan Pro",
                    "Updated description",
                    TenureType.MONTHS,
                    24,
                    new BigDecimal("15.5000"),
                    FeeType.FIXED,
                    new BigDecimal("2000.00"),
                    FeeType.PERCENTAGE,
                    new BigDecimal("0.007"),
                    FeeType.FIXED,
                    new BigDecimal("750.00"),
                    3,
                    7,
                    new BigDecimal("15000.00"),
                    new BigDecimal("750000.00"),
                    true
            );

            when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));
            when(productRepository.findFirstByName(updateRequest.name())).thenReturn(Optional.of(savedProduct));
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
            when(productMapper.toResponse(savedProduct)).thenReturn(productResponse);

            ResponseEntity<?> response = productService.update(productId, updateRequest);

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            verify(productRepository).save(savedProduct);
        }
    }

    @Nested
    @DisplayName("Find All Products Tests")
    class FindAllProductsTests {

        @Test
        @DisplayName("Should return all products when no search term")
        void shouldReturnAllProductsWhenNoSearchTerm() {
            when(productRepository.findAll(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.singletonList(savedProduct)));

            ResponseEntity<?> response = productService.findAll(null, 0, 10, "id", "asc");

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isInstanceOf(PaginatedResponse.class);
            verify(productRepository).findAll(any(Pageable.class));
            verify(productRepository, never()).searchByAllFields(any(), any());
        }

        @Test
        @DisplayName("Should search products when search term provided")
        void shouldSearchProductsWhenSearchTermProvided() {
            String searchTerm = "Business";
            when(productRepository.searchByAllFields(eq(searchTerm), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.singletonList(savedProduct)));

            ResponseEntity<?> response = productService.findAll(searchTerm, 0, 10, "id", "asc");

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            verify(productRepository).searchByAllFields(eq(searchTerm), any(Pageable.class));
        }

        @Test
        @DisplayName("Should trim search term before searching")
        void shouldTrimSearchTermBeforeSearching() {
            String searchTerm = "  Business  ";
            String trimmedTerm = "Business";
            when(productRepository.searchByAllFields(eq(trimmedTerm), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.singletonList(savedProduct)));

            productService.findAll(searchTerm, 0, 10, "id", "asc");

            verify(productRepository).searchByAllFields(eq(trimmedTerm), any(Pageable.class));
        }
    }
}
