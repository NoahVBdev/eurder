package org.switchfully.eurder.eurder.eurder.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.switchfully.eurder.eurder.common.domain.Address;
import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.customer.repository.CustomerRepository;
import org.switchfully.eurder.eurder.eurder.api.dto.request.CreateItemGroupDTO;
import org.switchfully.eurder.eurder.eurder.api.dto.request.CreateOrderDTO;
import org.switchfully.eurder.eurder.eurder.repository.OrderRepository;
import org.switchfully.eurder.eurder.helper.IntegrationTest;
import org.switchfully.eurder.eurder.item.domain.Item;
import org.switchfully.eurder.eurder.item.exception.ItemNotFoundException;
import org.switchfully.eurder.eurder.item.repository.ItemRepository;
import org.switchfully.eurder.eurder.security.TestAuth;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class PlaceOrderTransactionIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    OrderService orderService;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ItemRepository itemRepository;
    @MockitoSpyBean
    OrderRepository orderRepository;

    private Customer aCustomer() {
        return customerRepository.save(new Customer("Jan", "Peeters", "jan@mail.be", new Address("Nieuwstraat", "1", "1000", "Brussel", "Belgie"), "0470123456", "{noop}password"));
    }

    @Test
    void givenUnknownSecondItem_whenPlacingOrder_returnFirstItemStockUnchanged() {
        // Arrange
        Customer customer = aCustomer();
        Item kettle = itemRepository.save(new Item("Kettle", "Hot", new BigDecimal("15.00"), 5));
        CreateOrderDTO request = new CreateOrderDTO(customer.getId(), List.of(new CreateItemGroupDTO(kettle.getId(), 2), new CreateItemGroupDTO(UUID.randomUUID(), 1)));

        // Act
        assertThatThrownBy(() -> orderService.placeOrder(customer.getEmail(), request)).isInstanceOf(ItemNotFoundException.class);

        // Assert
        assertThat(itemRepository.findById(kettle.getId()).orElseThrow().getStock()).isEqualTo(5);
    }

    @Test
    void givenSavingOrderFails_whenPlacingOrder_returnStockUnchanged() {
        // Arrange
        Customer customer = aCustomer();
        Item kettle = itemRepository.save(new Item("Kettle", "Hot", new BigDecimal("15.00"), 5));
        CreateOrderDTO request = new CreateOrderDTO(customer.getId(), List.of(new CreateItemGroupDTO(kettle.getId(), 2)));
        doThrow(new IllegalStateException("Simulated database failure")).when(orderRepository).save(any());

        // Act
        assertThatThrownBy(() -> orderService.placeOrder(customer.getEmail(), request)).isInstanceOf(IllegalStateException.class);

        // Assert
        assertThat(itemRepository.findById(kettle.getId()).orElseThrow().getStock()).isEqualTo(5);
    }

    @Test
    void givenConcurrentModification_whenPlacingOrder_return409WithoutInternals() throws Exception {
        // Arrange
        Customer customer = aCustomer();
        Item kettle = itemRepository.save(new Item("Kettle", "Hot", new BigDecimal("15.00"), 5));
        CreateOrderDTO request = new CreateOrderDTO(customer.getId(),
                List.of(new CreateItemGroupDTO(kettle.getId(), 1)));
        doThrow(new ObjectOptimisticLockingFailureException(Item.class, kettle.getId()))
                .when(orderRepository).save(any());

        // Act + Assert
        mockMvc.perform(post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, TestAuth.basic(customer.getEmail(), "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(not(containsString("org.switchfully"))));
    }
}