package world.ezra.loan_management.customer;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import world.ezra.loan_management.common.dto.PaginatedResponse;
import world.ezra.loan_management.common.enums.PreferredChannel;
import world.ezra.loan_management.customer.internal.dto.CustomerRequest;
import world.ezra.loan_management.customer.internal.model.Customer;
import world.ezra.loan_management.customer.internal.repository.CustomerRepository;
import world.ezra.loan_management.customer.internal.service.CustomerServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * @author Alex Kiburu
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRequest validRequest;
    private Customer validCustomer;

    @BeforeEach
    void setUp() {
        validRequest = new CustomerRequest(
                "John",
                "Doe",
                "24512111",
                "john.doe@example.com",
                "0728123456",
                "123 Main St, Nairobi",
                LocalDate.of(1990, 1, 1),
                PreferredChannel.EMAIL,
                BigDecimal.valueOf(50000.00)
        );

        validCustomer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .nationalId("24512111")
                .email("john.doe@example.com")
                .phone("0728123456")
                .address("123 Main St, Nairobi")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .preferredChannel(PreferredChannel.EMAIL)
                .currentLoanLimit(BigDecimal.valueOf(50000.00))
                .build();
    }


    @Nested
    @DisplayName("Create Customer Tests")
    class CreateCustomerTests {

        @Test
        @DisplayName("Given valid customer request When create Then return success response")
        void shouldCreateCustomerSuccessfully() {
            // Given
            when(customerRepository.save(any(Customer.class))).thenReturn(validCustomer);

            // When
            ResponseEntity<?> response = customerService.create(validRequest);

            // Then
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isNotNull();

            // Verify repository was called
            verify(customerRepository, times(1)).save(any(Customer.class));
        }

        @Test
        @DisplayName("Given valid request When create Then save customer with correct data")
        void shouldSaveCustomerWithCorrectData() {
            // Given
            when(customerRepository.save(any(Customer.class))).thenReturn(validCustomer);

            // When
            customerService.create(validRequest);

            // Then
            verify(customerRepository).save(argThat(customer ->
                    customer.getFirstName().equals("John") &&
                            customer.getLastName().equals("Doe") &&
                            customer.getEmail().equals("john.doe@example.com") &&
                            customer.getPhone().equals("0728123456") &&
                            customer.getPreferredChannel().equals(PreferredChannel.EMAIL)
            ));
        }

        @Test
        @DisplayName("Given repository throws exception When create Then throw runtime exception")
        void shouldThrowExceptionWhenRepositoryFails() {
            // Given
            when(customerRepository.save(any(Customer.class))).thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertThatThrownBy(() -> customerService.create(validRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Database error");
        }
    }


    @Nested
    @DisplayName("Find All Customers Tests")
    class FindAllCustomersTests {

        @Test
        @DisplayName("Given no search term When findAll Then return all customers with pagination")
        void shouldReturnAllCustomersWhenNoSearchTerm() {
            // Given
            Page<@NonNull Customer> customerPage = new PageImpl<>(Collections.singletonList(validCustomer));
            when(customerRepository.findAll(any(Pageable.class))).thenReturn(customerPage);

            // When
            ResponseEntity<?> response = customerService.findAll(null, 0, 10, "id", "desc");

            // Then
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            verify(customerRepository, times(1)).findAll(any(Pageable.class));
            verify(customerRepository, never()).searchByAllFields(anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("Given search term When findAll Then return filtered results")
        void shouldReturnFilteredResultsWhenSearchTermProvided() {
            // Given
            String searchTerm = "John";
            Page<@NonNull Customer> customerPage = new PageImpl<>(Collections.singletonList(validCustomer));
            when(customerRepository.searchByAllFields(eq(searchTerm), any(Pageable.class))).thenReturn(customerPage);

            // When
            ResponseEntity<?> response = customerService.findAll(searchTerm, 0, 10, "id", "desc");

            // Then
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            verify(customerRepository, times(1)).searchByAllFields(eq(searchTerm), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Pagination Response Tests")
    class PaginationResponseTests {

        @Test
        @DisplayName("When findAll Then return PaginatedResponse with correct metadata")
        void shouldReturnPaginatedResponseWithCorrectMetadata() {
            // Given
            Page<@NonNull Customer> customerPage = new PageImpl<>(
                    Collections.singletonList(validCustomer),
                    PageRequest.of(0, 10),
                    25L  // total elements
            );
            when(customerRepository.findAll(any(Pageable.class))).thenReturn(customerPage);

            // When
            ResponseEntity<?> response = customerService.findAll(null, 0, 10, "id", "des");

            // Then
            assertThat(response.getBody()).isInstanceOf(PaginatedResponse.class);
            PaginatedResponse<?> paginatedResponse = (PaginatedResponse<?>) response.getBody();
            assertThat(paginatedResponse).isNotNull();
            assertThat(paginatedResponse.getPageNumber()).isEqualTo(0);
            assertThat(paginatedResponse.getPageSize()).isEqualTo(10);
            assertThat(paginatedResponse.getTotalElements()).isEqualTo(25);
            assertThat(paginatedResponse.getTotalPages()).isEqualTo(3);
            assertThat(paginatedResponse.isFirst()).isTrue();
            assertThat(paginatedResponse.isEmpty()).isFalse();
        }
    }
}
