package world.ezra.loan_management.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import world.ezra.loan_management.auth.dto.AuthRequest;
import world.ezra.loan_management.auth.dto.AuthResponse;
import world.ezra.loan_management.auth.service.JwtService;
import world.ezra.loan_management.auth.service.impl.AuthServiceImpl;

import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Alex Kiburu
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Tests")
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private UserDetailsService userDetailsService;
    @InjectMocks private AuthServiceImpl authService;

    private static final String TEST_USERNAME = "ezra";
    private static final String TEST_PASSWORD = "ezra";
    private static final String TEST_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test-token";
    private static final long TOKEN_EXPIRATION_MS = 86400000; // 24 hours

    private UserDetails userDetails;
    private AuthRequest validAuthRequest;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        userDetails = User.builder()
                .username(TEST_USERNAME)
                .password("$2a$10$OfYAeGvTzjwjC8HFDfB5NOEzq2CjYWUrHaHRrqEWcUIlP33CCldCa")
                .roles("USER")
                .build();

        validAuthRequest = new AuthRequest(TEST_USERNAME, TEST_PASSWORD);
        authentication = new UsernamePasswordAuthenticationToken(TEST_USERNAME, TEST_PASSWORD);
    }

    @Nested
    @DisplayName("Successful Login")
    class SuccessfulLogin {

        @Test
        @DisplayName("Should return auth response with valid JWT token")
        void shouldReturnAuthResponseWithValidJwtToken() {
            // Given
            Date expirationDate = new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(userDetailsService.loadUserByUsername(TEST_USERNAME))
                    .thenReturn(userDetails);
            when(jwtService.generateToken(userDetails))
                    .thenReturn(TEST_JWT_TOKEN);
            when(jwtService.extractExpiration(TEST_JWT_TOKEN))
                    .thenReturn(expirationDate);

            // When
            ResponseEntity<?> response = authService.login(validAuthRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            AuthResponse authResponse = (AuthResponse) response.getBody();
            assertThat(authResponse)
                    .isNotNull()
                    .satisfies(res -> {
                        assertThat(res.token()).isEqualTo(TEST_JWT_TOKEN);
                        assertThat(res.expiresIn()).isBetween(
                                Duration.ofHours(23).getSeconds(),
                                Duration.ofHours(24).getSeconds()
                        );
                    });

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(userDetailsService).loadUserByUsername(TEST_USERNAME);
            verify(jwtService).generateToken(userDetails);
            verify(jwtService).extractExpiration(TEST_JWT_TOKEN);
            verifyNoMoreInteractions(authenticationManager, userDetailsService, jwtService);
        }
    }

    @Nested
    @DisplayName("Failed Login Attempts")
    class FailedLogin {

        @Test
        @DisplayName("Should throw BadCredentialsException when password is incorrect")
        void shouldThrowBadCredentialsExceptionForInvalidPassword() {
            // Given
            AuthRequest invalidRequest = new AuthRequest(TEST_USERNAME, "wrongpassword");
            String expectedErrorMessage = "Invalid username or password";

            doThrow(new BadCredentialsException(expectedErrorMessage))
                    .when(authenticationManager)
                    .authenticate(any(UsernamePasswordAuthenticationToken.class));

            // When & Then
            assertThatThrownBy(() -> authService.login(invalidRequest))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage(expectedErrorMessage);

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verifyNoInteractions(userDetailsService, jwtService);
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when username does not exist")
        void shouldThrowBadCredentialsExceptionForNonexistentUser() {
            // Given
            AuthRequest invalidRequest = new AuthRequest("nonexistent", "password");
            String expectedErrorMessage = "Invalid username or password";

            doThrow(new BadCredentialsException(expectedErrorMessage))
                    .when(authenticationManager)
                    .authenticate(any(UsernamePasswordAuthenticationToken.class));

            // When & Then
            assertThatThrownBy(() -> authService.login(invalidRequest))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage(expectedErrorMessage);

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verifyNoInteractions(userDetailsService, jwtService);
        }
    }
}