package org.switchfully.eurder.eurder.item.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.switchfully.eurder.eurder.helper.IntegrationTest;
import org.switchfully.eurder.eurder.item.domain.Item;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
class ItemOptimisticLockingIntegrationTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("A stale copy of an item cannot overwrite a newer change")
    void givenTwoCopiesOfSameItem_whenBothAreSaved_throwOnSecondSave() {
        // Arrange: two requests load the same item, each in its own transaction
        UUID id = itemRepository.save(new Item("Fridge", "Cold", new BigDecimal("500.00"), 1)).getId();
        Item firstRequest = itemRepository.findById(id).orElseThrow();
        Item secondRequest = itemRepository.findById(id).orElseThrow();

        // Act: both sell the last fridge
        firstRequest.decreaseStock(1);
        secondRequest.decreaseStock(1);
        itemRepository.save(firstRequest);

        // Assert: the second write is rejected instead of silently overwriting the first
        assertThatThrownBy(() -> itemRepository.save(secondRequest))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
        assertThat(itemRepository.findById(id).orElseThrow().getStock()).isZero();
    }
}