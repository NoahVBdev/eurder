package org.switchfully.eurder.eurder.eurder.repository;

import org.springframework.stereotype.Repository;
import org.switchfully.eurder.eurder.eurder.domain.Order;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();

    public Order save(Order order) {
        orders.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(orders.get(id));
    }
}

