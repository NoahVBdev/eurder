package org.switchfully.eurder.eurder.eurder.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.switchfully.eurder.eurder.common.domain.Address;
import org.switchfully.eurder.eurder.customer.exception.CustomerNotFoundException;
import org.switchfully.eurder.eurder.customer.service.CustomerOrderingDetails;
import org.switchfully.eurder.eurder.customer.service.CustomerService;
import org.switchfully.eurder.eurder.eurder.api.dto.request.CreateItemGroupDTO;
import org.switchfully.eurder.eurder.eurder.api.dto.request.CreateOrderDTO;
import org.switchfully.eurder.eurder.eurder.domain.ItemGroup;
import org.switchfully.eurder.eurder.eurder.domain.Order;
import org.switchfully.eurder.eurder.eurder.repository.OrderRepository;
import org.switchfully.eurder.eurder.item.domain.Item;
import org.switchfully.eurder.eurder.item.service.ItemService;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 20);
    private static final Clock FIXED_CLOCK = Clock.fixed(TODAY.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    private static final UUID CUSTOMER_ID = UUID.fromString("4841b498-acc6-4e62-a58d-43f94b4cc1e7");
    private static final Address ADDRESS = new Address("Rue Neuve", "100B", "1000", "Brussels", "Belgium");
    private static final String EMAIL = "jane@example.com";

    @Mock
    OrderRepository orderRepository;
    @Mock
    ItemService itemService;
    @Mock
    CustomerService customerService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, itemService, customerService, new OrderMapper(), FIXED_CLOCK);
    }

    @Test
    void givenValidOrder_whenPlacingOrder_saveOrderWithAddressSnapshotAndToday() {
        // Arrange
        Item fridge = new Item("Fridge", "Cold", new BigDecimal("500.00"), 10);
        CreateOrderDTO request = new CreateOrderDTO(CUSTOMER_ID, List.of(new CreateItemGroupDTO(fridge.getId(), 2)));

        when(customerService.getOrderingDetails(EMAIL)).thenReturn(new CustomerOrderingDetails(CUSTOMER_ID, ADDRESS));
        when(itemService.createItemGroups(request, TODAY)).thenReturn(List.of(new ItemGroup(fridge, 2, TODAY)));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        orderService.placeOrder(EMAIL, request);

        // Assert
        ArgumentCaptor<Order> saved = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(saved.capture());

        assertThat(saved.getValue().getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(saved.getValue().getShippingAddress()).isEqualTo(ADDRESS);
        assertThat(saved.getValue().getOrderDate()).isEqualTo(TODAY);
        assertThat(saved.getValue().getItemGroups()).hasSize(1);
    }

    @Test
    void givenUnknownCustomer_whenPlacingOrder_throwAndSaveNothing() {
        // Arrange
        CreateOrderDTO request = new CreateOrderDTO(CUSTOMER_ID, List.of(new CreateItemGroupDTO(UUID.randomUUID(), 1)));
        when(customerService.getOrderingDetails(EMAIL)).thenThrow(new CustomerNotFoundException(EMAIL));

        // Act + Assert
        assertThatThrownBy(() -> orderService.placeOrder(EMAIL, request))
                .isInstanceOf(CustomerNotFoundException.class);
        verify(itemService, never()).createItemGroups(any(), any());
        verify(orderRepository, never()).save(any());
    }
}