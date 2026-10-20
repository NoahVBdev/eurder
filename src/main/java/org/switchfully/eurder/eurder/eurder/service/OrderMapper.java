package org.switchfully.eurder.eurder.eurder.service;

import org.springframework.stereotype.Component;
import org.switchfully.eurder.eurder.eurder.api.dto.response.ItemGroupDTO;
import org.switchfully.eurder.eurder.eurder.api.dto.response.OrderDTO;
import org.switchfully.eurder.eurder.eurder.domain.ItemGroup;
import org.switchfully.eurder.eurder.eurder.domain.Order;

@Component
public class OrderMapper {

    public OrderDTO toDTO(Order order) {
        return new OrderDTO(order.getId(), order.getCustomerId(), order.getItemGroups().stream().map(this::toDTO).toList(), order.getTotalPrice());
    }

    public ItemGroupDTO toDTO(ItemGroup itemGroup) {
        return new ItemGroupDTO(itemGroup.getItemId(), itemGroup.getItemName(), itemGroup.getAmount(), itemGroup.getPricePerUnit(), itemGroup.getTotalPrice(), itemGroup.getShippingDate());
    }
}
