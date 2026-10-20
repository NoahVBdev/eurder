package org.switchfully.eurder.eurder.item.service;

import org.springframework.stereotype.Service;
import org.switchfully.eurder.eurder.item.api.dto.CreateItemDTO;
import org.switchfully.eurder.eurder.item.api.dto.ItemDTO;
import org.switchfully.eurder.eurder.item.repository.ItemRepository;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemService(ItemRepository itemRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    public ItemDTO createItem(CreateItemDTO createItemDTO) {
        return itemMapper.toDTO(itemRepository.save(itemMapper.toDomain(createItemDTO)));
    }
}
