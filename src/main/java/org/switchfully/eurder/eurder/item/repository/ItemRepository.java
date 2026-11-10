package org.switchfully.eurder.eurder.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.switchfully.eurder.eurder.item.domain.Item;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
}
