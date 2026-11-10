package org.switchfully.eurder.eurder.eurder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.switchfully.eurder.eurder.common.domain.Address;
import org.switchfully.eurder.eurder.eurder.exception.OrderException;
import org.switchfully.eurder.eurder.item.domain.Item;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {
    private static final UUID CUSTOMER_ID = UUID.fromString("4841b498-acc6-4e62-a58d-43f94b4cc1e7");
    private static final Address ADDRESS = new Address("Rue Neuve", "100B", "1000", "Brussels", "Belgium");
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 20);

    private static ItemGroup anItemGroup() {
        Item fridge = new Item("Fridge", "Cold", new BigDecimal("500.00"), 10);
        return new ItemGroup(fridge, 1, TODAY);
    }

    @Test
    @DisplayName("Total price is the sum of all item groups")
    void givenMultipleItemGroups_whenGettingTotalPrice_returnSum() {
        // Arrange
        Item fridge = new Item("Fridge", "Cold", new BigDecimal("500.00"), 10);
        Item oven = new Item("Oven", "Hot", new BigDecimal("300.00"), 10);

        // Act
        Order order = new Order(CUSTOMER_ID, ADDRESS, List.of(
                new ItemGroup(fridge, 2, TODAY),
                new ItemGroup(oven, 1, TODAY)), TODAY);

        // Assert
        assertThat(order.getTotalPrice()).isEqualByComparingTo("1300.00");
    }

    @Test
    @DisplayName("An order stores its shipping address and order date")
    void givenValidData_whenCreatingOrder_returnAddressAndDate() {
        // Arrange + Act
        Order order = new Order(CUSTOMER_ID, ADDRESS, List.of(anItemGroup()), TODAY);

        // Assert
        assertThat(order.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(order.getShippingAddress()).isEqualTo(ADDRESS);
        assertThat(order.getOrderDate()).isEqualTo(TODAY);
    }

    @Test
    @DisplayName("Every item group points back to its order")
    void givenItemGroups_whenCreatingOrder_returnItemGroupsLinkedToOrder() {
        // Arrange
        ItemGroup first = anItemGroup();
        ItemGroup second = anItemGroup();

        // Act
        Order order = new Order(CUSTOMER_ID, ADDRESS, List.of(first, second), TODAY);

        // Assert
        assertThat(order.getItemGroups()).allSatisfy(group -> assertThat(group.getOrder()).isSameAs(order));
    }

    @Test
    @DisplayName("The item groups of an order cannot be modified from outside")
    void givenOrder_whenAddingToItemGroups_throwUnsupportedOperation() {
        // Arrange
        Order order = new Order(CUSTOMER_ID, ADDRESS, List.of(anItemGroup()), TODAY);

        // Act + Assert
        assertThatThrownBy(() -> order.getItemGroups().add(anItemGroup()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Changing the original list afterwards does not change the order")
    void givenOriginalList_whenModifiedAfterCreation_returnOrderUnchanged() {
        // Arrange
        List<ItemGroup> original = new ArrayList<>(List.of(anItemGroup()));
        Order order = new Order(CUSTOMER_ID, ADDRESS, original, TODAY);

        // Act
        original.add(anItemGroup());

        // Assert
        assertThat(order.getItemGroups()).hasSize(1);
    }

    @Test
    @DisplayName("An order without item groups throws an OrderException")
    void givenNoItemGroups_whenCreatingOrder_throwOrderException() {
        assertThatThrownBy(() -> new Order(CUSTOMER_ID, ADDRESS, List.of(), TODAY))
                .isInstanceOf(OrderException.class);
    }

    @Test
    @DisplayName("An order with null item groups throws an OrderException")
    void givenNullItemGroups_whenCreatingOrder_throwOrderException() {
        assertThatThrownBy(() -> new Order(CUSTOMER_ID, ADDRESS, null, TODAY))
                .isInstanceOf(OrderException.class);
    }

    @Test
    @DisplayName("An order without customer throws an OrderException")
    void givenNullCustomer_whenCreatingOrder_throwOrderException() {
        assertThatThrownBy(() -> new Order(null, ADDRESS, List.of(anItemGroup()), TODAY))
                .isInstanceOf(OrderException.class);
    }

    @Test
    @DisplayName("An order without shipping address throws an OrderException")
    void givenNullAddress_whenCreatingOrder_throwOrderException() {
        assertThatThrownBy(() -> new Order(CUSTOMER_ID, null, List.of(anItemGroup()), TODAY))
                .isInstanceOf(OrderException.class);
    }

    @Test
    @DisplayName("An order without order date throws an OrderException")
    void givenNullOrderDate_whenCreatingOrder_throwOrderException() {
        assertThatThrownBy(() -> new Order(CUSTOMER_ID, ADDRESS, List.of(anItemGroup()), null))
                .isInstanceOf(OrderException.class);
    }
}