package world.ezra.loan_management.customer.internal.service;

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
import world.ezra.loan_management.customer.api.CustomerApi;
import world.ezra.loan_management.customer.internal.dto.CustomerRequest;
import world.ezra.loan_management.customer.internal.model.Customer;
import world.ezra.loan_management.customer.internal.model.CustomerFinancialMetrics;
import world.ezra.loan_management.customer.internal.repository.CustomerFinancialMetricsRepository;
import world.ezra.loan_management.customer.internal.repository.CustomerRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerApi {

    private final CustomerRepository customerRepository;
    private final CustomerFinancialMetricsRepository customerFinancialMetricsRepository;

    @Override
    public ResponseEntity<?> findAll(String searchTerm, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // If search term is null or empty, return all customers
        if (StringUtils.isEmpty(searchTerm)) {
            Page<@NonNull Customer> dataPage = customerRepository.findAll(pageable);
            return ResponseEntity.ok(new PaginatedResponse<>(dataPage));
        }

        Page<@NonNull Customer> dataPage = customerRepository.searchByAllFields(searchTerm.trim(), pageable);
        return ResponseEntity.ok(new PaginatedResponse<>(dataPage));
    }

    @Override
    public ResponseEntity<?> create(CustomerRequest request) {
        try {
            // Create customer
            Customer customer = Customer.builder()
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .nationalId(request.nationalId())
                    .email(request.email())
                    .phone(request.phone())
                    .address(request.address())
                    .dateOfBirth(request.dateOfBirth())
                    .preferredChannel(request.preferredChannel())
                    .build();
            Customer savedCustomer = customerRepository.save(customer);

            // Initialize financial metrics for the new customer
            CustomerFinancialMetrics metrics = CustomerFinancialMetrics.builder()
                    .customerId(savedCustomer.getId())
                    .totalLoansTaken(0)
                    .totalAmountBorrowed(BigDecimal.ZERO)
                    .totalAmountRepaid(BigDecimal.ZERO)
                    .onTimeRepaymentRate(new BigDecimal("1.0000"))
                    .numberOfDefaults(0)
                    .totalDaysLate(0)
                    .averageDaysLate(BigDecimal.ZERO)
                    .build();

            customerFinancialMetricsRepository.save(metrics);

            GenericResponse response = GenericResponse.builder()
                    .status("00")
                    .message("Customer created successfully")
                    .data(savedCustomer)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    /**
     * Update customer financial metrics after repayment
     */
    @Override
    public void updateCustomerMetrics(Long customerId, BigDecimal amountRepaid) {
        CustomerFinancialMetrics metrics = customerFinancialMetricsRepository.findByCustomerId(customerId)
                .orElse(null);

        if (metrics != null) {
            metrics.setTotalAmountRepaid(metrics.getTotalAmountRepaid().add(amountRepaid));
            metrics.setLastRepaymentDate(LocalDate.now());
            customerFinancialMetricsRepository.save(metrics);
            log.info("Updated customer metrics for customer: {}", customerId);
        }
    }
}
