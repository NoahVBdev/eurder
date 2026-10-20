package org.switchfully.eurder.eurder.eurder.domain;

import org.jspecify.annotations.NonNull;
import org.switchfully.eurder.eurder.eurder.exception.ItemGroupException;
import org.switchfully.eurder.eurder.item.domain.Item;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class ItemGroup {
    private static final int DAYS_WHEN_IN_STOCK = 1;
    private static final int DAYS_WHEN_OUT_OF_STOCK = 7;

    private final UUID itemId;
    private final String itemName; // Snapshot
    private final BigDecimal pricePerUnit; // Snapshot
    private final int amount;
    private final LocalDate shippingDate;

    public ItemGroup(Item item, int amount, LocalDate orderDate) {
        if (amount <= 0) throw new ItemGroupException("Amount must be greater than 0");
        this.itemId = item.getId();
        this.itemName = item.getName();
        this.pricePerUnit = item.getPrice();
        this.amount = amount;
        this.shippingDate = calculateShippingDate(item, amount, orderDate);
    }

    private @NonNull LocalDate calculateShippingDate(Item item, int amount, LocalDate orderDate) {
        return item.getAmount() >= amount
                ? orderDate.plusDays(DAYS_WHEN_IN_STOCK)
                : orderDate.plusDays(DAYS_WHEN_OUT_OF_STOCK);
    }

    public UUID getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDate getShippingDate() {
        return shippingDate;
    }

    public @NonNull BigDecimal getTotalPrice() {
        return pricePerUnit.multiply(BigDecimal.valueOf(amount));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemGroup itemGroup = (ItemGroup) o;
        return amount == itemGroup.amount && Objects.equals(itemId, itemGroup.itemId) && Objects.equals(itemName, itemGroup.itemName) && Objects.equals(pricePerUnit, itemGroup.pricePerUnit) && Objects.equals(shippingDate, itemGroup.shippingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, itemName, pricePerUnit, amount, shippingDate);
    }
}
