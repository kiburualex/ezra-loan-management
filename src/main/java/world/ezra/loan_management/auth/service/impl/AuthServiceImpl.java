package world.ezra.loan_management.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import world.ezra.loan_management.auth.dto.AuthRequest;
import world.ezra.loan_management.auth.dto.AuthResponse;
import world.ezra.loan_management.auth.service.AuthService;
import world.ezra.loan_management.auth.service.JwtService;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public ResponseEntity<?> login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
            String jwt = jwtService.generateToken(userDetails);

            Date expirationDate = jwtService.extractExpiration(jwt);
            Instant expiresAt = expirationDate.toInstant();
            long expiresInSeconds = TimeUnit.MILLISECONDS.toSeconds(
                    expirationDate.getTime() - System.currentTimeMillis()
            );

            AuthResponse response = new AuthResponse(
                    jwt,
                    expiresInSeconds,
                    TimeUnit.SECONDS.name(),
                    expiresAt
            );

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
