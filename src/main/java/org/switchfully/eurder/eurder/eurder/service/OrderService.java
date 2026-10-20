package org.switchfully.eurder.eurder.eurder.service;

import org.springframework.stereotype.Service;
import org.switchfully.eurder.eurder.customer.exception.CustomerNotFoundException;
import org.switchfully.eurder.eurder.customer.repository.CustomerRepository;
import org.switchfully.eurder.eurder.eurder.api.dto.request.CreateOrderDTO;
import org.switchfully.eurder.eurder.eurder.api.dto.response.ItemGroupDTO;
import org.switchfully.eurder.eurder.eurder.api.dto.response.OrderDTO;
import org.switchfully.eurder.eurder.eurder.domain.ItemGroup;
import org.switchfully.eurder.eurder.eurder.domain.Order;
import org.switchfully.eurder.eurder.eurder.repository.OrderRepository;
import org.switchfully.eurder.eurder.item.domain.Item;
import org.switchfully.eurder.eurder.item.exception.ItemNotFoundException;
import org.switchfully.eurder.eurder.item.repository.ItemRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;
    private final Clock clock; // inject a Clock bean so tests can fix "today"

    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository, CustomerRepository customerRepository, OrderMapper orderMapper, Clock clock) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.customerRepository = customerRepository;
        this.orderMapper = orderMapper;
        this.clock = clock;
    }

    public OrderDTO placeOrder(CreateOrderDTO createOrderDTO) {
        customerRepository.findById(createOrderDTO.customerId()).orElseThrow(() -> new CustomerNotFoundException(createOrderDTO.customerId().toString()));
        LocalDate today = LocalDate.now(clock);

        // Create an ItemGroup for each DTO and decrease the stock
        List<ItemGroup> itemGroups = createOrderDTO.itemGroups().stream()
                .map(itemGroupDTO -> {
                    Item item = itemRepository.findById(itemGroupDTO.itemId()).orElseThrow(() -> new ItemNotFoundException(itemGroupDTO.itemId().toString()));
                    ItemGroup itemGroup = new ItemGroup(item, itemGroupDTO.amount(), today);
                    item.decreaseStock(itemGroupDTO.amount());
                    return itemGroup;
                })
                .toList();
        
        Order order = orderRepository.save(new Order(createOrderDTO.customerId(), itemGroups));
        return orderMapper.toDTO(order);
    }
}
