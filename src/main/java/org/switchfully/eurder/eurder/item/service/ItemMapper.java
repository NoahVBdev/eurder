package org.switchfully.eurder.eurder.item.service;

import org.springframework.stereotype.Component;
import org.switchfully.eurder.eurder.item.api.dto.CreateItemDTO;
import org.switchfully.eurder.eurder.item.api.dto.ItemDTO;
import org.switchfully.eurder.eurder.item.domain.Item;

@Component
public class ItemMapper {
    public Item toDomain(CreateItemDTO createItemDTO) {
        return new Item(createItemDTO.name(), createItemDTO.description(), createItemDTO.price(), createItemDTO.stock());
    }

    public ItemDTO toDTO(Item item) {
        return new ItemDTO(item.getId(), item.getName(), item.getDescription(), item.getPrice(), item.getStock());
    }
}
