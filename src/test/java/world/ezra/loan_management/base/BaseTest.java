package world.ezra.loan_management.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import world.ezra.loan_management.auth.service.JwtService;
import world.ezra.loan_management.customer.api.CustomerApi;
import world.ezra.loan_management.product.api.ProductApi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Alex Kiburu
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class BaseTest {

    @Autowired
    protected MockMvc mockMvc;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private CustomerApi customerApi;
    @MockitoBean
    private ProductApi productApi;
    @MockitoBean
    private PulsarTemplate<?> pulsarTemplate;
    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected static final String USERNAME = "ezra";
    protected static final String PASSWORD = "ezra";
    protected String authToken;

    @BeforeEach
    void setUpBase() throws Exception {
        authToken = loginAndExtractToken();
    }

    protected String loginAndExtractToken() throws Exception {
        String loginJson = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(BaseTest.USERNAME, BaseTest.PASSWORD);

        String responseBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(responseBody).get("token").asText();
    }

    protected <T> T parseResponse(MvcResult result, Class<T> responseType) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), responseType);
    }
}
