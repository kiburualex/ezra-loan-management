package world.ezra.loan_management.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @author Alex Kiburu
 */
public record AuthRequest(
        @NotBlank(message = "Username [username] is mandatory")
        String username,

        @NotBlank(message = "Password [password] is mandatory")
        String password
) {
}
