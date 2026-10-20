package org.switchfully.eurder.eurder.eurder.domain;

import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.eurder.exception.OrderException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final UUID customerId;
    private final List<ItemGroup> itemGroups;

    public Order(UUID customerId, List<ItemGroup> itemGroups) {
        if (itemGroups == null || itemGroups.isEmpty())
            throw new OrderException("An order must contain one or more item groups");
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.itemGroups = List.copyOf(itemGroups);
    }

    public BigDecimal getTotalPrice() {
        return itemGroups.stream()
                .map(ItemGroup::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<ItemGroup> getItemGroups() {
        return itemGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
