package org.switchfully.eurder.eurder.item.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.switchfully.eurder.eurder.helper.IntegrationTest;
import org.switchfully.eurder.eurder.item.domain.Item;
import org.switchfully.eurder.eurder.item.repository.ItemRepository;
import org.switchfully.eurder.eurder.security.TestAuth;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@IntegrationTest
class ItemControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TestAuth testAuth;

    // Item for every call
    private static Map<String, Object> validItem() {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", "Fridge Coolio Z");
        item.put("description", "Energy-efficient fridge-freezer, 300 litres");
        item.put("price", new BigDecimal("749.95"));
        item.put("stock", 12);
        return item;
    }


    // Helper for post requests
    private ResultActions postItem(Map<String, Object> item) throws Exception {
        return mockMvc.perform(post("/items")
                .header(HttpHeaders.AUTHORIZATION, testAuth.admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)));
    }

    @Nested
    @DisplayName("Creating an item")
    class CreateItem {

        @Test
        @DisplayName("with valid data returns 201 Is Created and item")
        void givenValidData_returns201andItem() throws Exception {
            // Arrange + Act + Assert
            String response = postItem(validItem())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.name").value("Fridge Coolio Z"))
                    .andExpect(jsonPath("$.description").value("Energy-efficient fridge-freezer, 300 litres"))
                    .andExpect(jsonPath("$.price").value(749.95))
                    .andExpect(jsonPath("$.stock").value(12))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Act
            UUID id = UUID.fromString(JsonPath.read(response, "$.id"));
            Item stored = itemRepository.findById(id).orElse(null);

            // Assert
            assertThat(stored).isNotNull();
            assertThat(stored.getName()).isEqualTo("Fridge Coolio Z");
            assertThat(stored.getDescription()).isEqualTo("Energy-efficient fridge-freezer, 300 litres");
            assertThat(stored.getPrice()).isEqualByComparingTo("749.95");
            assertThat(stored.getStock()).isEqualTo(12);
        }

        @Test
        @DisplayName("with blank name returns 400 Bad Request")
        void givenBlankName_returns400() throws Exception {
            // Arrange
            Map<String, Object> item = validItem();
            item.put("name", " ");

            // Act + Assert
            postItem(item).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("with blank description returns 400 Bad Request")
        void givenBlankDescription_returns400() throws Exception {
            // Arrange
            Map<String, Object> item = validItem();
            item.put("description", " ");

            // Act + Assert
            postItem(item).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("with null price returns 400 Bad Request")
        void givenNullPrice_returns400() throws Exception {
            // Arrange
            Map<String, Object> item = validItem();
            item.put("price", null);

            // Act + Assert
            postItem(item).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("with negative price returns 400 Bad Request")
        void givenNegativePrice_returns400() throws Exception {
            // Arrange
            Map<String, Object> item = validItem();
            item.put("price", new BigDecimal("-749.95"));

            // Act + Assert
            postItem(item).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("with price as text returns 400 Bad Request")
        void givenPriceAsText_returns400() throws Exception {
            // Arrange
            Map<String, Object> item = validItem();
            item.put("price", "very very cheap");

            // Act + Assert
            postItem(item).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("with null stock returns 400 Bad Request")
        void givenBlankStock_returns400() throws Exception {
            // Arrange
            Map<String, Object> item = validItem();
            item.put("stock", null);

            // Act + Assert
            postItem(item).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("with negative stock returns 400 Bad Request")
        void givenNegativeStock_returns400() throws Exception {
            // Arrange
            Map<String, Object> item = validItem();
            item.put("stock", -12);

            // Act + Assert
            postItem(item).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("with missing stock returns 400 Bad Request")
        void givenMissingStock_returns400() throws Exception {
            // Arrange
            Map<String, Object> item = validItem();
            item.remove("stock");

            // Act + Assert
            postItem(item).andExpect(status().isBadRequest());
        }
    }
}