package org.switchfully.eurder.eurder.customer.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.switchfully.eurder.eurder.customer.repository.CustomerRepository;
import org.switchfully.eurder.eurder.helper.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class CustomerRegistrationRaceConditionIntegrationTest {

    private static final String CUSTOMER_JSON =  """
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

    @Autowired
    MockMvc mockMvc;

    @MockitoSpyBean
    CustomerRepository customerRepository;

    @Test
    void registerCustomer_whenConcurrentRegistrationPassedEmailCheck_returns409() throws Exception {
        // Arrange: the first registration succeeds
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUSTOMER_JSON))
                .andExpect(status().isCreated());

        // Simulate a concurrent registration that passed the check before the first one was saved
        doReturn(false).when(customerRepository).existsByEmail(anyString());

        // Act + Assert: the database constraint catches it, and the API still answers 409
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUSTOMER_JSON))
                .andExpect(status().isConflict());

        assertThat(customerRepository.count()).isEqualTo(1);
    }
}