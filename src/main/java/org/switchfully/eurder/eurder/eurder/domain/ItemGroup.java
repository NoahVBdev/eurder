package org.switchfully.eurder.eurder.eurder.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.jspecify.annotations.NonNull;
import org.switchfully.eurder.eurder.common.domain.BaseEntity;
import org.switchfully.eurder.eurder.eurder.exception.ItemGroupException;
import org.switchfully.eurder.eurder.item.domain.Item;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "item_group")
public class ItemGroup extends BaseEntity {
    private static final int DAYS_WHEN_IN_STOCK = 1;
    private static final int DAYS_WHEN_OUT_OF_STOCK = 7;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eurder_id", nullable = false, updatable = false)
    private Order order;
    private UUID itemId;
    private String itemName;        // Snapshot
    private BigDecimal pricePerUnit; // Snapshot
    private int amount;
    private LocalDate shippingDate;

    public ItemGroup(Item item, int amount, LocalDate orderDate) {
        if (amount <= 0) throw new ItemGroupException("Amount must be greater than 0");
        this.itemId = item.getId();
        this.itemName = item.getName();
        this.pricePerUnit = item.getPrice();
        this.amount = amount;
        this.shippingDate = calculateShippingDate(item, amount, orderDate);
    }

    protected ItemGroup() {
    }

    /**
     * Links this item group to its order. Package-private: only {@link Order} calls this,
     * so both sides of the relationship always stay in sync.
     */
    void assignTo(Order order) {
        if (this.order != null && this.order != order)
            throw new ItemGroupException("Item group already belongs to another order");
        this.order = order;
    }

    private @NonNull LocalDate calculateShippingDate(Item item, int amount, LocalDate orderDate) {
        return item.getStock() >= amount
                ? orderDate.plusDays(DAYS_WHEN_IN_STOCK)
                : orderDate.plusDays(DAYS_WHEN_OUT_OF_STOCK);
    }

    public Order getOrder() {
        return order;
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
}