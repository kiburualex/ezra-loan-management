package world.ezra.loan_management.auth.service;

import org.springframework.http.ResponseEntity;
import world.ezra.loan_management.auth.dto.AuthRequest;

/**
 * @author Alex Kiburu
 */
public interface AuthService {
    ResponseEntity<?> login(AuthRequest request);
}
