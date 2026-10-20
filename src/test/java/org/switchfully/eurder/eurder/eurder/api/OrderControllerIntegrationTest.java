package org.switchfully.eurder.eurder.eurder.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.customer.repository.CustomerRepository;
import org.switchfully.eurder.eurder.eurder.api.dto.request.CreateItemGroupDTO;
import org.switchfully.eurder.eurder.eurder.api.dto.request.CreateOrderDTO;
import org.switchfully.eurder.eurder.item.domain.Item;
import org.switchfully.eurder.eurder.item.repository.ItemRepository;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    private static final UUID NON_EXISTING_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerRepository customerRepository;

    // ---------- helpers ----------
    @Autowired
    private ItemRepository itemRepository;

    private Customer aCustomer() {
        return customerRepository.save(new Customer("Jan", "Peeters", "jan@mail.be",
                new Customer.Address("Nieuwstraat", "1", "1000", "Brussel", "Belgie"),
                "0470123456"));
    }

    private Item anItem(String name, String price, int stock) {
        return itemRepository.save(new Item(name, "Description", new BigDecimal(price), stock));
    }

    private ResultActions placeOrder(CreateOrderDTO request) throws Exception {
        return mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @Nested
    @DisplayName("Happy paths")
    class HappyPaths {

        @Test
        @DisplayName("Placing order with valid data returns 201 with total price")
        void placeOrder_happyPath_returns201WithTotalPrice() throws Exception {
            Customer customer = aCustomer();
            Item item = anItem("Fridge", "10.00", 5);

            CreateOrderDTO request = new CreateOrderDTO(customer.getId(),
                    List.of(new CreateItemGroupDTO(item.getId(), 2)));

            placeOrder(request)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.totalPrice").value(20.00))
                    .andExpect(jsonPath("$.itemGroups.length()").value(1))
                    .andExpect(jsonPath("$.itemGroups[0].shippingDate")
                            .value(LocalDate.now().plusDays(1).toString()));
        }

        @Test
        @DisplayName("Ordering more than the stock sets shipping date to next week")
        void placeOrder_notEnoughStock_shippingDateIsNextWeek() throws Exception {
            Customer customer = aCustomer();
            Item item = anItem("Oven", "300.00", 2);

            CreateOrderDTO request = new CreateOrderDTO(customer.getId(),
                    List.of(new CreateItemGroupDTO(item.getId(), 3)));

            placeOrder(request)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.itemGroups[0].shippingDate")
                            .value(LocalDate.now().plusDays(7).toString()));
        }

        @Test
        @DisplayName("Ordering exactly the stock amount ships next day")
        void placeOrder_exactStock_shippingDateIsNextDay() throws Exception {
            Customer customer = aCustomer();
            Item item = anItem("Toaster", "25.00", 4);

            CreateOrderDTO request = new CreateOrderDTO(customer.getId(),
                    List.of(new CreateItemGroupDTO(item.getId(), 4)));

            placeOrder(request)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.itemGroups[0].shippingDate")
                            .value(LocalDate.now().plusDays(1).toString()));
        }

        @Test
        @DisplayName("Order with multiple item groups sums the total price")
        void placeOrder_multipleItemGroups_totalIsSum() throws Exception {
            Customer customer = aCustomer();
            Item fridge = anItem("Fridge", "500.00", 10);
            Item lamp = anItem("Lamp", "20.00", 10);

            CreateOrderDTO request = new CreateOrderDTO(customer.getId(), List.of(
                    new CreateItemGroupDTO(fridge.getId(), 2),   // 1000.00
                    new CreateItemGroupDTO(lamp.getId(), 3)));   //   60.00

            placeOrder(request)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.itemGroups.length()").value(2))
                    .andExpect(jsonPath("$.totalPrice").value(1060.00));
        }

        @Test
        @DisplayName("Placing an order decreases the item stock")
        void placeOrder_decreasesStock() throws Exception {
            Customer customer = aCustomer();
            Item item = anItem("Kettle", "15.00", 5);

            CreateOrderDTO request = new CreateOrderDTO(customer.getId(),
                    List.of(new CreateItemGroupDTO(item.getId(), 2)));

            placeOrder(request).andExpect(status().isCreated());

            Item updated = itemRepository.findById(item.getId()).orElseThrow();
            assertThat(updated.getAmount()).isEqualTo(3);
        }

        @Test
        @DisplayName("Order keeps the price at time of ordering, even if item price changes later")
        void placeOrder_priceIsSnapshot() throws Exception {
            Customer customer = aCustomer();
            Item item = anItem("Mixer", "50.00", 5);

            CreateOrderDTO request = new CreateOrderDTO(customer.getId(),
                    List.of(new CreateItemGroupDTO(item.getId(), 1)));

            placeOrder(request)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.itemGroups[0].pricePerUnit").value(50.00))
                    .andExpect(jsonPath("$.itemGroups[0].itemName").value("Mixer"));
        }
    }

    @Nested
    @DisplayName("Failure paths")
    class FailurePaths {

        @Test
        @DisplayName("Unknown customer returns 404")
        void placeOrder_unknownCustomer_returns404() throws Exception {
            Item item = anItem("Oven", "300.00", 5);

            CreateOrderDTO request = new CreateOrderDTO(NON_EXISTING_ID,
                    List.of(new CreateItemGroupDTO(item.getId(), 1)));

            placeOrder(request).andExpect(status().isNotFound());
        }


        @Test
        @DisplayName("Unknown item returns 404")
        void placeOrder_unknownItem_returns404() throws Exception {
            Customer customer = aCustomer();

            CreateOrderDTO request = new CreateOrderDTO(customer.getId(),
                    List.of(new CreateItemGroupDTO(NON_EXISTING_ID, 1)));

            placeOrder(request).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Amount zero returns 400")
        void placeOrder_amountZero_returns400() throws Exception {
            Customer customer = aCustomer();
            Item item = anItem("Lamp", "20.00", 5);

            CreateOrderDTO request = new CreateOrderDTO(customer.getId(),
                    List.of(new CreateItemGroupDTO(item.getId(), 0)));

            placeOrder(request).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative amount returns 400")
        void placeOrder_negativeAmount_returns400() throws Exception {
            Customer customer = aCustomer();
            Item item = anItem("Lamp", "20.00", 5);

            CreateOrderDTO request = new CreateOrderDTO(customer.getId(),
                    List.of(new CreateItemGroupDTO(item.getId(), -1)));

            placeOrder(request).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Order without item groups returns 400")
        void placeOrder_noItemGroups_returns400() throws Exception {
            Customer customer = aCustomer();

            CreateOrderDTO request = new CreateOrderDTO(customer.getId(), List.of());

            placeOrder(request).andExpect(status().isBadRequest());
        }
    }
}