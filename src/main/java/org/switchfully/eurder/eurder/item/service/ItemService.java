package org.switchfully.eurder.eurder.item.service;

import org.springframework.stereotype.Service;
import org.switchfully.eurder.eurder.eurder.api.dto.request.CreateOrderDTO;
import org.switchfully.eurder.eurder.eurder.domain.ItemGroup;
import org.switchfully.eurder.eurder.item.api.dto.CreateItemDTO;
import org.switchfully.eurder.eurder.item.api.dto.ItemDTO;
import org.switchfully.eurder.eurder.item.domain.Item;
import org.switchfully.eurder.eurder.item.exception.ItemNotFoundException;
import org.switchfully.eurder.eurder.item.repository.ItemRepository;

import java.time.LocalDate;
import java.util.List;

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

    public List<ItemGroup> createItemGroups(CreateOrderDTO createOrderDTO, LocalDate today) {
        return createOrderDTO.itemGroups().stream()
                .map(itemGroupDTO -> {
                    Item item = itemRepository.findById(itemGroupDTO.itemId()).orElseThrow(() -> new ItemNotFoundException(itemGroupDTO.itemId().toString()));
                    ItemGroup itemGroup = new ItemGroup(item, itemGroupDTO.amount(), today);
                    item.decreaseStock(itemGroupDTO.amount());
                    return itemGroup;
                })
                .toList();
    }
}
