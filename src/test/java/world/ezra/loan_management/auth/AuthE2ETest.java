package world.ezra.loan_management.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthE2ETest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();   // Spring Boot auto-configures this
    private static final String USERNAME = "ezra";
    private static final String PASSWORD = "ezra";

    @Test
    @DisplayName("Given valid credentials When login Then return JWT with expiration details")
    void shouldLoginSuccessfullyAndReturnJwt() throws Exception {
        // Given
        String loginJson = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(USERNAME, PASSWORD);

        // When & Then
        String tokenResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyString())))
                .andExpect(jsonPath("$.expiresIn", greaterThan(0)))
                .andExpect(jsonPath("$.timeUnit", is("SECONDS")))
                .andExpect(jsonPath("$.expiresAt", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Optional: Print token for debugging
        System.out.println("Generated Token: " + tokenResponse);
    }

    @Test
    @DisplayName("Given valid JWT token When access protected /hello Then return personalized greeting")
    void shouldAccessProtectedHelloEndpointWithValidToken() throws Exception {
        // Given - Obtain a valid token first
        String token = loginAndExtractToken(USERNAME, PASSWORD);

        // When & Then
        mockMvc.perform(get("/api/hello")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Hello, " + USERNAME + "! Welcome to the protected endpoint."
                ));
    }

    // ====================== FAILURE TESTS ======================

    @Test
    @DisplayName("Given invalid credentials When login Then return 403 Forbidden")
    void shouldReturn401ForInvalidLogin() throws Exception {
        // Given
        String invalidLoginJson = """
                {
                    "username": "ezra",
                    "password": "wrongpassword"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginJson))
                .andExpect(status().isForbidden());   // 401
    }

    @Test
    @DisplayName("Given no token When access protected /hello Then return 403 Forbidden")
    void shouldReturn401WhenAccessingHelloWithoutToken() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Given invalid token When access protected /hello Then return 401 Unauthorized")
    void shouldReturn401WithInvalidToken() throws Exception {
        // create invalid token with valid format (header, payload and signature
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        mockMvc.perform(get("/api/hello")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    // ====================== HELPER METHOD ======================

    /**
     * Reusable helper to login and extract only the token string
     */
    private String loginAndExtractToken(String username, String password) throws Exception {
        String loginJson = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(username, password);

        String responseBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract token using Jackson
        return objectMapper.readTree(responseBody)
                .get("token")
                .asText();
    }
}