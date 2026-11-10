package org.switchfully.eurder.eurder.eurder.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.switchfully.eurder.eurder.eurder.exception.ItemGroupException;
import org.switchfully.eurder.eurder.item.domain.Item;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemGroupTest {
    @Test
    @DisplayName("Shipping date for an item in stock is next day")
    void shippingDate_whenItemInStock_isNextDay() {
        Item item = new Item("Fridge", "Cold", new BigDecimal("500.00"), 10);
        LocalDate orderDate = LocalDate.of(2026, 10, 20);

        ItemGroup group = new ItemGroup(item, 3, orderDate);

        assertThat(group.getShippingDate()).isEqualTo(LocalDate.of(2026, 10, 21));
    }

    @Test
    @DisplayName("Shipping date for an item NOT in stock is next week")
    void shippingDate_whenNotEnoughStock_isNextWeek() {
        Item item = new Item("Fridge", "Cold", new BigDecimal("500.00"), 2);
        LocalDate orderDate = LocalDate.of(2026, 10, 20);

        ItemGroup group = new ItemGroup(item, 3, orderDate);

        assertThat(group.getShippingDate()).isEqualTo(LocalDate.of(2026, 10, 27));
    }

    @Test
    @DisplayName("Total price is price per unit times stock ordered")
    void totalPrice_isPriceTimesAmount() {
        Item item = new Item("Fridge", "Cold", new BigDecimal("500.00"), 10);

        ItemGroup group = new ItemGroup(item, 3, LocalDate.now());

        assertThat(group.getTotalPrice()).isEqualByComparingTo("1500.00");
    }

    @Test
    @DisplayName("An item group with stock zero or less throws an ItemGroupException")
    void amountZeroOrLess_throws() {
        Item item = new Item("Fridge", "Cold", new BigDecimal("500.00"), 10);
        assertThatThrownBy(() -> new ItemGroup(item, 0, LocalDate.now()))
                .isInstanceOf(ItemGroupException.class);
    }
}