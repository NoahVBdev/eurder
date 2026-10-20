package org.switchfully.eurder.eurder.item.repository;

import org.switchfully.eurder.eurder.item.domain.Item;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {

    /**
     * Saves the given item.
     *
     * <p>If no item with the same id exists yet, the item is added. Otherwise the
     * existing item is replaced by the given one.</p>
     *
     * <p>Callers should continue working with the returned instance, which may
     * differ from the one passed in.</p>
     *
     * @param item the item to save; must not be {@code null}
     * @return the saved item
     * @throws NullPointerException if {@code item} is {@code null}
     */
    Item save(Item item);

    /**
     * Finds the item with the given id.
     *
     * @param id the id of the item to find; must not be {@code null}
     * @return an {@code Optional} containing the item if one exists with this id,
     *         or an empty {@code Optional} otherwise
     * @throws NullPointerException if {@code id} is {@code null}
     */
    Optional<Item> findById(UUID id);

    /**
     * Deletes all items.
     *
     * <p>This is irreversible. It exists to give tests a clean repository and
     * is not meant to be used by application code.</p>
     */
    void deleteAll();
}
