package org.switchfully.eurder.eurder.eurder.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.switchfully.eurder.eurder.common.domain.Address;
import org.switchfully.eurder.eurder.common.domain.BaseEntity;
import org.switchfully.eurder.eurder.eurder.exception.OrderException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "eurder")
public class Order extends BaseEntity {

    private UUID customerId;

    @Embedded
    private Address shippingAddress; // Snapshot, see decision in #23

    private LocalDate orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemGroup> itemGroups = new ArrayList<>();

    public Order(UUID customerId, Address shippingAddress, List<ItemGroup> itemGroups, LocalDate orderDate) {
        if (customerId == null) throw new OrderException("Customer is required");
        if (shippingAddress == null) throw new OrderException("Shipping address is required");
        if (orderDate == null) throw new OrderException("Order date is required");
        if (itemGroups == null || itemGroups.isEmpty())
            throw new OrderException("An order must contain one or more item groups");

        this.customerId = customerId;
        this.shippingAddress = shippingAddress;
        this.orderDate = orderDate;
        itemGroups.forEach(this::addItemGroup);
    }

    protected Order() {
    }

    private void addItemGroup(ItemGroup itemGroup) {
        itemGroup.assignTo(this);
        this.itemGroups.add(itemGroup);
    }

    public BigDecimal getTotalPrice() {
        return itemGroups.stream()
                .map(ItemGroup::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public List<ItemGroup> getItemGroups() {
        return Collections.unmodifiableList(itemGroups);
    }
}