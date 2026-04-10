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
import world.ezra.loan_management.customer.internal.repository.CustomerRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerApi {

    private final CustomerRepository customerRepository;

    @Override
    public ResponseEntity<?> findAll(String searchTerm, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // If search term is null or empty, return all customers
        if (StringUtils.isEmpty(searchTerm)) {
            Page<@NonNull Customer> customerPage = customerRepository.findAll(pageable);
            return ResponseEntity.ok(new PaginatedResponse<>(customerPage));
        }

        Page<@NonNull Customer> customerPage = customerRepository.searchByAllFields(searchTerm.trim(), pageable);
        return ResponseEntity.ok(new PaginatedResponse<>(customerPage));
    }

    @Override
    public ResponseEntity<?> create(CustomerRequest request) {
        try {
            // Build and save
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
            Customer createdCustomer = customerRepository.save(customer);
            GenericResponse response = GenericResponse.builder()
                    .status("00")
                    .message("Customer created successfully")
                    .data(createdCustomer)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
