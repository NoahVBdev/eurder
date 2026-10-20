package org.switchfully.eurder.eurder.customer.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.switchfully.eurder.eurder.customer.repository.InMemoryCustomerRepository;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerIntegrationTest {

    private static final String VALID_CUSTOMER = """
            {
              "firstName": "Jane",
              "lastName": "Smith",
              "email": "jane.smith@example.com",
              "address": { "street": "Rue Neuve", "houseNumber": "1",
                           "postalCode": "1000", "city": "Brussels", "country": "Belgium" },
              "phoneNumber": "+32 470 12 34 56"
            }
            """;

    @Autowired
    private InMemoryCustomerRepository customerRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Nested
    @DisplayName("Creating a customer:")
    class createCustomer {

        @Test
        @DisplayName("with valid data returns 201 Is Created and customer")
        void createCustomer_givenValidData_returns201andCustomer() throws Exception {
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CUSTOMER))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.email").value("jane.smith@example.com"))
                    .andExpect(jsonPath("$.address.city").value("Brussels"));

        }

        @Test
        @DisplayName("with invalid email returns 400 Bad Request")
        void createCustomer_withInvalidEmail_returns400() throws Exception {
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CUSTOMER.replace("jane.smith@example.com", "invalid-email")))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("with email already in use returns 409 Bad Conflict")
        void createCustomer_withValidEmailAlreadyInUse_returns400() throws Exception {
            // Arrange: register the customer once
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CUSTOMER))
                    .andExpect(status().isCreated());

            // Act + Assert: registering the same email again is rejected
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CUSTOMER))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(containsString("A customer with email jane.smith@example.com already exists")));
        }
    }
}