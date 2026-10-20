package org.switchfully.eurder.eurder.item.repository;

import org.switchfully.eurder.eurder.item.domain.Item;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {
    Item save(Item item);
    Optional<Item> findById(UUID id);
    void deleteAll();
}
