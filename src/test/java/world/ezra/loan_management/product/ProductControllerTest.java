package world.ezra.loan_management.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import world.ezra.loan_management.base.BaseTest;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Alex Kiburu
 */
public class ProductControllerTest extends BaseTest {
    @Nested
    @DisplayName("Create Products Tests")
    class CreateProductsTests {

        @Test
        @DisplayName("Given valid request When create product Then return success response")
        void shouldCreateProductSuccessfully() throws Exception {
            String productRequest = """
                    {
                        "name": "Business Loan Pro",
                        "description": "Premium business loan",
                        "tenureType": "MONTHS",
                        "tenureValue": 12,
                        "interestRate": 12.50,
                        "serviceFeeType": "FIXED",
                        "serviceFeeValue": 1500.00,
                        "dailyFeeType": "PERCENTAGE",
                        "dailyFeeValue": 0.005,
                        "lateFeeType": "FIXED",
                        "lateFeeValue": 500.00,
                        "daysAfterDueForLateFee": 5,
                        "daysAfterDueForDailyFee": 10,
                        "minLoanAmount": 10000.00,
                        "maxLoanAmount": 500000.00,
                        "active": true
                    }
                    """;

            mockMvc.perform(post("/api/products")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("00")))
                    .andExpect(jsonPath("$.message", is("Product created successfully")))
                    .andExpect(jsonPath("$.data.id", notNullValue()))
                    .andExpect(jsonPath("$.data.name", is("Business Loan Pro")));
        }

        @Test
        @DisplayName("Given duplicate product name When create product Then return error")
        void shouldReturnErrorForDuplicateProductName() throws Exception {
            String productRequest = """
                    {
                        "name": "Duplicate Product",
                        "description": "Test product",
                        "tenureType": "MONTHS",
                        "tenureValue": 6,
                        "interestRate": 10.0000,
                        "serviceFeeType": "FIXED",
                        "serviceFeeValue": 1000.00,
                        "dailyFeeType": "PERCENTAGE",
                        "dailyFeeValue": 0.005,
                        "lateFeeType": "FIXED",
                        "lateFeeValue": 250.00,
                        "daysAfterDueForLateFee": 3,
                        "daysAfterDueForDailyFee": 7,
                        "minLoanAmount": 5000.00,
                        "maxLoanAmount": 100000.00,
                        "active": true
                    }
                    """;

            // First creation
            mockMvc.perform(post("/api/products")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productRequest))
                    .andExpect(status().isOk());

            // Duplicate creation
            mockMvc.perform(post("/api/products")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productRequest))
                    .andExpect(status().is5xxServerError())
                    .andExpect(jsonPath("$.message", containsString("already exists")));
        }
    }

    @Nested
    @DisplayName("Update Products Tests")
    class UpdateProductsTests {

        @Test
        @DisplayName("Given valid request When update product Then return success response")
        void shouldUpdateProductSuccessfully() throws Exception {
            // First create a product
            String createRequest = """
                    {
                        "name": "Product To Update",
                        "description": "Original description",
                        "tenureType": "MONTHS",
                        "tenureValue": 12,
                        "interestRate": 12.50,
                        "serviceFeeType": "FIXED",
                        "serviceFeeValue": 1500.00,
                        "dailyFeeType": "PERCENTAGE",
                        "dailyFeeValue": 0.005,
                        "lateFeeType": "FIXED",
                        "lateFeeValue": 500.00,
                        "daysAfterDueForLateFee": 5,
                        "daysAfterDueForDailyFee": 10,
                        "minLoanAmount": 10000.00,
                        "maxLoanAmount": 500000.00,
                        "active": true
                    }
                    """;

            String createResponse = mockMvc.perform(post("/api/products")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createRequest))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long productId = objectMapper.readTree(createResponse)
                    .get("data")
                    .get("id")
                    .asLong();

            // Update the product
            String updateRequest = """
                    {
                        "name": "Updated Product Name",
                        "description": "Updated description",
                        "tenureType": "MONTHS",
                        "tenureValue": 24,
                        "interestRate": 15.5000,
                        "serviceFeeType": "FIXED",
                        "serviceFeeValue": 2000.00,
                        "dailyFeeType": "PERCENTAGE",
                        "dailyFeeValue": 0.007,
                        "lateFeeType": "FIXED",
                        "lateFeeValue": 750.00,
                        "daysAfterDueForLateFee": 3,
                        "daysAfterDueForDailyFee": 7,
                        "minLoanAmount": 15000.00,
                        "maxLoanAmount": 750000.00,
                        "active": true
                    }
                    """;

            mockMvc.perform(put("/api/products/{id}", productId)
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("00")))
                    .andExpect(jsonPath("$.message", is("Product updated successfully")))
                    .andExpect(jsonPath("$.data.name", is("Updated Product Name")))
                    .andExpect(jsonPath("$.data.tenureValue", is(24)));
        }

        @Test
        @DisplayName("Given non-existent product ID When update Then return error")
        void shouldReturnErrorForNonExistentProduct() throws Exception {
            String updateRequest = """
                    {
                        "name": "Non Existent Product",
                        "description": "Test",
                        "tenureType": "MONTHS",
                        "tenureValue": 12,
                        "interestRate": 12.50,
                        "serviceFeeType": "FIXED",
                        "serviceFeeValue": 1500.00,
                        "lateFeeType": "FIXED",
                        "lateFeeValue": 500.00,
                        "daysAfterDueForLateFee": 5,
                        "minLoanAmount": 10000.00,
                        "maxLoanAmount": 500000.00,
                        "active": true
                    }
                    """;

            mockMvc.perform(put("/api/products/{id}", 99999L)
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateRequest))
                    .andExpect(status().is5xxServerError())
                    .andExpect(jsonPath("$.message", containsString("Product not found")));
        }
    }

    @Nested
    @DisplayName("Find All Products Tests")
    class FindAllProductsTests {

        @Test
        @DisplayName("Given no search term When get all products Then return paginated response")
        void shouldReturnPaginatedProducts() throws Exception {
            mockMvc.perform(get("/api/products")
                            .header("Authorization", "Bearer " + authToken)
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "id")
                            .param("sortDirection", "desc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", notNullValue()))
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.pageSize", is(10)))
                    .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(0)));
        }

        @Test
        @DisplayName("Given search term When get products Then return filtered results")
        void shouldSearchProductsByTerm() throws Exception {
            // Create a product to search for
            String productRequest = """
                    {
                        "name": "Unique Searchable Product",
                        "description": "Test product",
                        "tenureType": "MONTHS",
                        "tenureValue": 12,
                        "interestRate": 12.50,
                        "serviceFeeType": "FIXED",
                        "serviceFeeValue": 1500.00,
                        "dailyFeeType": "PERCENTAGE",
                        "dailyFeeValue": 0.005,
                        "lateFeeType": "FIXED",
                        "lateFeeValue": 500.00,
                        "daysAfterDueForLateFee": 5,
                        "daysAfterDueForDailyFee": 10,
                        "minLoanAmount": 10000.00,
                        "maxLoanAmount": 500000.00,
                        "active": true
                    }
                    """;

            mockMvc.perform(post("/api/products")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productRequest))
                    .andExpect(status().isOk());

            // Search for the product
            mockMvc.perform(get("/api/products")
                            .header("Authorization", "Bearer " + authToken)
                            .param("searchTerm", "Unique Searchable"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", notNullValue()))
                    .andExpect(jsonPath("$.content[*].name", hasItem("Unique Searchable Product")));
        }
    }
}
