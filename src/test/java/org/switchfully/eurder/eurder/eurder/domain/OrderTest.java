package org.switchfully.eurder.eurder.eurder.domain;


import org.junit.jupiter.api.Test;
import org.switchfully.eurder.eurder.eurder.exception.OrderException;
import org.switchfully.eurder.eurder.item.domain.Item;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {
    private static final UUID CUSTOMER_ID = UUID.fromString("4841b498-acc6-4e62-a58d-43f94b4cc1e7");

    @Test
    void totalPrice_isSumOfItemGroups() {
        Item fridge = new Item("Fridge", "Cold", new BigDecimal("500.00"), 10);
        Item oven = new Item("Oven", "Hot", new BigDecimal("300.00"), 10);
        LocalDate today = LocalDate.now();

        Order order = new Order(CUSTOMER_ID, List.of(
                new ItemGroup(fridge, 2, today),
                new ItemGroup(oven, 1, today)));

        assertThat(order.getTotalPrice()).isEqualByComparingTo("1300.00");
    }

    @Test
    void orderWithoutItemGroups_throws() {
        assertThatThrownBy(() -> new Order(CUSTOMER_ID, List.of()))
                .isInstanceOf(OrderException.class);
    }

}