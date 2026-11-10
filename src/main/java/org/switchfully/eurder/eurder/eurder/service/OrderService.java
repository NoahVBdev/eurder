package org.switchfully.eurder.eurder.eurder.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.switchfully.eurder.eurder.customer.service.CustomerOrderingDetails;
import org.switchfully.eurder.eurder.customer.service.CustomerService;
import org.switchfully.eurder.eurder.eurder.api.dto.request.CreateOrderDTO;
import org.switchfully.eurder.eurder.eurder.api.dto.response.OrderDTO;
import org.switchfully.eurder.eurder.eurder.domain.ItemGroup;
import org.switchfully.eurder.eurder.eurder.domain.Order;
import org.switchfully.eurder.eurder.eurder.repository.OrderRepository;
import org.switchfully.eurder.eurder.item.service.ItemService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemService itemService;
    private final CustomerService customerService;
    private final OrderMapper orderMapper;
    private final Clock clock; // inject a Clock bean so tests can fix "today"

    public OrderService(OrderRepository orderRepository, ItemService itemService, CustomerService customerService, OrderMapper orderMapper, Clock clock) {
        this.orderRepository = orderRepository;
        this.itemService = itemService;
        this.customerService = customerService;
        this.orderMapper = orderMapper;
        this.clock = clock;
    }

    public OrderDTO placeOrder(String customerEmail, CreateOrderDTO createOrderDTO) {
        CustomerOrderingDetails customer = customerService.getOrderingDetails(customerEmail);
        LocalDate today = LocalDate.now(clock);

        List<ItemGroup> itemGroups = itemService.createItemGroups(createOrderDTO, today);
        Order order = orderRepository.save(new Order(customer.customerId(), customer.address(), itemGroups, today));
        return orderMapper.toDTO(order);
    }
}
