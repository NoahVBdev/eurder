package org.switchfully.eurder.eurder.item.repository;

import org.springframework.stereotype.Repository;
import org.switchfully.eurder.eurder.item.domain.Item;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the in-memory repository for {@link Item} objects.
 */
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<UUID, Item> items = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     *
     * <p>Stores the item in the in-memory map, keyed by its id. The same instance
     * that was passed in is returned.</p>
     */
    @Override
    public Item save(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Looks the item up in the in-memory map, keyed by its id.</p>
     */
    @Override
    public Optional<Item> findById(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(items.get(id));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Clears the in-memory map.</p>
     */
    @Override
    public void deleteAll() {
        items.clear();
    }
}
