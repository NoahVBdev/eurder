package org.switchfully.eurder.eurder.item.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.switchfully.eurder.eurder.common.domain.BaseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "item")
public class Item extends BaseEntity {

    private String name;
    private String description;
    private BigDecimal price;
    private int stock;

    public Item(String name, String description, BigDecimal price, int stock) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description is required");
        if (price == null) throw new IllegalArgumentException("Price is required");
        if (price.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Price can not be lower than 0");
        if (price.scale() > 2) throw new IllegalArgumentException("Price with 2 or less decimals required");
        if (stock < 0) throw new IllegalArgumentException("Amount can not be lower than 0");

        this.name = name;
        this.description = description;
        this.price = price.setScale(2, RoundingMode.HALF_EVEN);
        this.stock = stock;
    }

    protected Item() {
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

    public Integer getStock() {
        return stock;
    }

    public void decreaseStock(int orderedAmount) {
        this.stock = Math.max(0, this.stock - orderedAmount);
    }

}
