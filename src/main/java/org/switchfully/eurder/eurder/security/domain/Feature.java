package org.switchfully.eurder.eurder.security.domain;

public enum Feature {
    // ADMIN
    VIEW_ALL_CUSTOMERS("view all customers"),
    VIEW_CUSTOMER("view customers details"),
    ADD_ITEM("add an item"),
    UPDATE_ITEM("update an item"),
    VIEW_SHIPPING_TODAY("view shipping today"),
    VIEW_ITEM_OVERVIEW("view an an item today"),

    // CUSTOMER
    PLACE_ORDER("order one or more items"),
    REORDER("reorder an existing an order"),
    VIEW_ORDER_REPORT("view report of orders");

    private final String description;

    Feature(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
