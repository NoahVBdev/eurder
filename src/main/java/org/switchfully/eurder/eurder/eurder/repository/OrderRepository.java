package org.switchfully.eurder.eurder.eurder.repository;

import org.switchfully.eurder.eurder.eurder.domain.Order;

public interface OrderRepository {
    Order save(Order order);
}