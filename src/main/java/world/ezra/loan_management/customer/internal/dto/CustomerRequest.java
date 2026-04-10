package world.ezra.loan_management.customer.internal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import world.ezra.loan_management.common.enums.PreferredChannel;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Alex Kiburu
 */
public record CustomerRequest(
        @NotBlank(message = "First Name [firstName] is mandatory")
        String firstName,

        @NotBlank(message = "Last Name [lastName] is mandatory")
        String lastName,

        @NotBlank(message = "National ID [nationalId] is mandatory")
        String nationalId,

        @NotBlank(message = "Email [email] is mandatory")
        @Email(message = "Email [email] should be valid")
        String email,

        @NotBlank(message = "Phone [phone] is mandatory")
        @Pattern(regexp = "^(\\+254|254|0)[0-9]{9}$",
                message = "Phone [phone] should be valid format: +254728506150, 254728506150, 0728506150, or 0114506150")
        String phone,

        @NotBlank(message = "Address[address] is mandatory")
        String address,

        @Past(message = "Date Of Birth [dateOfBirth] must be in the past")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dateOfBirth,

        @NotNull(message = "Preferred Channel [preferredChannel] is mandatory")
        PreferredChannel preferredChannel,

        @NotNull(message = "Current Loan Limit [currentLoanLimit] is mandatory")
        @DecimalMin(value = "0.0",
                message = "Current Loan Limit [currentLoanLimit] must be >= 0")
        @DecimalMax(value = "10000000.0",
                message = "Current Loan Limit [currentLoanLimit] must be <= 10,000,000")
        BigDecimal currentLoanLimit
) {
}
