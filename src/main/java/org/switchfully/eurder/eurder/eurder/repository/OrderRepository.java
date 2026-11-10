package org.switchfully.eurder.eurder.eurder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.switchfully.eurder.eurder.eurder.domain.Order;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}