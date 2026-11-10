package org.switchfully.eurder.eurder.customer.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.customer.exception.CustomerNotFoundException;
import org.switchfully.eurder.eurder.customer.repository.CustomerRepository;
import org.switchfully.eurder.eurder.customer.service.CustomerService;
import org.switchfully.eurder.eurder.helper.IntegrationTest;
import org.switchfully.eurder.eurder.security.TestAuth;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
class CustomerControllerIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(CustomerControllerIntegrationTest.class);

    private static final String VALID_CUSTOMER = """
            {
              "firstName": "Jane",
              "lastName": "Smith",
              "email": "jane.smith@example.com",
              "address": { "street": "Rue Neuve", "houseNumber": "1",
                           "postalCode": "1000", "city": "Brussels", "country": "Belgium" },
              "phoneNumber": "+32 470 12 34 56",
              "password": "{noop}password"
            }
            """;

    private static final String VALID_CUSTOMER_2 = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "email": "johnsmith80@other.example.com",
              "address": { "street": "Nieuwstraat", "houseNumber": "100",
                           "postalCode": "1000", "city": "Brussels", "country": "Belgium" },
              "phoneNumber": "+32 470 12 34 57",
              "password": "{noop}password"
            }
            """;
    @Autowired
    TestAuth testAuth;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private MockMvc mockMvc;

    private static String customerJsonWithEmail(String email) {
        return """
                {
                  "firstName": "Jane",
                  "lastName": "Smith",
                  "email": "%s",
                  "address": {
                    "street": "Rue Neuve",
                    "houseNumber": "100B",
                    "postalCode": "1000",
                    "city": "Brussels",
                    "country": "Belgium"
                  },
                  "phoneNumber": "+32425447444",
                  "password": "secret-password"
                }
                """.formatted(email);
    }

    private ResultActions register(String email) throws Exception {
        return mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJsonWithEmail(email)));
    }

    @Nested
    @DisplayName("Creating a customer:")
    class CreateCustomer {

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

    @Nested
    @DisplayName("Getting customers")
    class GetCustomers {

        @Test
        @DisplayName("returns a list of all registered customers")
        void getAllCustomers_returns200AndListOfCustomers() throws Exception {
            // Arrange
            mockMvc.perform(post("/customers")
                            .header(HttpHeaders.AUTHORIZATION, testAuth.admin())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CUSTOMER))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/customers")
                            .header(HttpHeaders.AUTHORIZATION, testAuth.admin())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CUSTOMER_2))
                    .andExpect(status().isCreated());

            // Act + Assert
            mockMvc.perform(get("/customers")
                            .header(HttpHeaders.AUTHORIZATION, testAuth.admin())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[?(@.email == 'jane.smith@example.com')]").exists())
                    .andExpect(jsonPath("$[?(@.email == 'johnsmith80@other.example.com')]").exists());
        }

        @Test
        @DisplayName("when no customers registered returns 404 not found")
        void getAllCustomers_whenNoCustomersRegistered_returns404() throws Exception {
            // Act + Assert
            mockMvc.perform(get("/customers")
                            .header(HttpHeaders.AUTHORIZATION, testAuth.admin())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Getting a single customer")
    class GetCustomer {

        @Test
        @DisplayName("with known valid id returns 201 and customer")
        void getCustomer_givenCustomerExists_returns200AndCustomer() throws Exception {
            // Arrange
            log.debug("Logging getCustomer_givenCustomerExists_returns200AndCustomer String response=%s".formatted(testAuth.admin()));
            String response = mockMvc.perform(post("/customers")
                            .header(HttpHeaders.AUTHORIZATION, testAuth.admin())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CUSTOMER))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();


            UUID id = UUID.fromString(JsonPath.read(response, "$.id"));
            Customer stored = customerRepository.findById(id).orElse(null);
            // Assert
            assertThat(stored).isNotNull();
            assertThat(stored.getFirstName()).isEqualTo("Jane");
            assertThat(stored.getLastName()).isEqualTo("Smith");
            assertThat(stored.getEmail()).isEqualTo("jane.smith@example.com");
            assertThat(stored.getAddress()).isNotNull();
            assertThat(stored.getPhoneNumber()).isEqualTo("+32 470 12 34 56");
        }

        @Test
        @DisplayName("with unknown valid id returns 404 CouldNotFindCustomerException")
        void getCustomer_unknownId_returns404CouldNotFindCustomerException() throws Exception {
            // Arrange
            UUID unknownId = UUID.randomUUID();

            // Act
            mockMvc.perform(get("/customers/{id}", unknownId)
                            .header(HttpHeaders.AUTHORIZATION, testAuth.admin()))
                    .andExpect(status().isNotFound());

            // Assert
            assertThatThrownBy(() -> customerService.findCustomerById(unknownId)).isInstanceOf(CustomerNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Email case")
    class EmailCase {

        @Test
        @DisplayName("registering with a mixed-case email stores and returns it in lowercase")
        void register_mixedCaseEmail_returnsLowercase() throws Exception {
            register("Jane.Smith@Example.COM")
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("jane.smith@example.com"));
        }

        @Test
        @DisplayName("registering an existing email in another case returns 409")
        void register_existingEmailInOtherCase_returns409() throws Exception {
            register("jane.smith@example.com").andExpect(status().isCreated());

            register("Jane.Smith@Example.COM").andExpect(status().isConflict());
        }
    }
}