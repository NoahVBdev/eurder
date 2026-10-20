package org.switchfully.eurder.eurder.item.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Item {
    private final UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private int amount;

    public Item(String name, String description, BigDecimal price, int amount) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description is required");
        if (price == null) throw new IllegalArgumentException("Price is required");
        if (price.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Price can not be lower than 0");
        if (price.scale() > 2) throw new IllegalArgumentException("Price with 2 or less decimals required");
        if (amount < 0) throw new IllegalArgumentException("Amount can not be lower than 0");

        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
