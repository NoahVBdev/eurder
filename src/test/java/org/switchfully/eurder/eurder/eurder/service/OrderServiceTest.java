package org.switchfully.eurder.eurder.eurder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.switchfully.eurder.eurder.customer.repository.CustomerRepository;
import org.switchfully.eurder.eurder.eurder.repository.OrderRepository;
import org.switchfully.eurder.eurder.item.repository.ItemRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    OrderMapper orderMapper;

    Clock fixedClock = Clock.fixed(
            LocalDate.of(2026, 10, 20).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault());

    OrderService service = new OrderService(orderRepository, itemRepository, customerRepository, orderMapper, fixedClock);

}