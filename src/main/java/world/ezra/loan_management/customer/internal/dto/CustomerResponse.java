package world.ezra.loan_management.customer.internal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Alex Kiburu
 */
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerResponse {
    Long customerId;
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    LocalDate dateOfBirth;
    BigDecimal currentLoanLimit;
    LocalDateTime  createdAt;
    LocalDateTime updatedAt;

}
