package world.ezra.loan_management.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import world.ezra.loan_management.base.BaseTest;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Alex Kiburu
 */
public class CustomerControllerTest extends BaseTest {
    @Test
    @DisplayName("Given valid request When create customer Then return success response")
    void shouldCreateCustomerSuccessfully() throws Exception {
        String customerRequest = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com",
                    "phone": "0728123456",
                    "nationalId": "1829128129",
                    "address": "123 Main St, Nairobi",
                    "dateOfBirth": "1990-01-01",
                    "preferredChannel": "EMAIL",
                    "currentLoanLimit": 50000.00
                }
                """;

        mockMvc.perform(post("/api/customers")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("00")));
    }
}
