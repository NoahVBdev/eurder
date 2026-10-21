package org.switchfully.eurder.eurder.eurder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.switchfully.eurder.eurder.customer.service.CustomerService;
import org.switchfully.eurder.eurder.eurder.repository.OrderRepository;
import org.switchfully.eurder.eurder.item.service.ItemService;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

class OrderServiceTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemService itemService;
    @Autowired
    CustomerService customerService;
    @Autowired
    OrderMapper orderMapper;

    Clock fixedClock = Clock.fixed(
            LocalDate.of(2026, 10, 20).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault());

    OrderService service = new OrderService(orderRepository, itemService, customerService, orderMapper, fixedClock);
}