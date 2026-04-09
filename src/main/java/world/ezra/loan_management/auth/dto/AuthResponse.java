package world.ezra.loan_management.auth.dto;

import java.time.Instant;

/**
 * @author Alex Kiburu
 */
public record AuthResponse (
        String token,
        long expiresIn,      // in seconds
        String timeUnit,     // "SECONDS"
        Instant expiresAt
){
}
