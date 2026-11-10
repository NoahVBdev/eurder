package org.switchfully.eurder.eurder.customer.service;

import org.switchfully.eurder.eurder.common.domain.Address;

import java.util.UUID;

/**
 * What another feature needs from a customer to place an order.
 * Contains only shared types, so callers never depend on the Customer entity.
 */
public record CustomerOrderingDetails(UUID customerId, Address address) {
}