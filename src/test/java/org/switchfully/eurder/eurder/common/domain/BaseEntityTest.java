package org.switchfully.eurder.eurder.common.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

    // Minimal subclasses: BaseEntity is abstract
    private static class TestEntity extends BaseEntity {
    }

    private static class OtherTestEntity extends BaseEntity {
    }

    // Simulates an entity loaded from the database with a known id
    private static <T extends BaseEntity> T withId(T entity, UUID id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    @Test
    @DisplayName("A new entity has an id before it is saved")
    void givenNewEntity_whenGettingId_returnId() {
        // Arrange + Act
        TestEntity entity = new TestEntity();

        // Assert
        assertThat(entity.getId()).isNotNull();
    }

    @Test
    @DisplayName("Two new entities get different ids")
    void givenTwoNewEntities_whenComparingIds_returnDifferentIds() {
        // Arrange
        TestEntity first = new TestEntity();
        TestEntity second = new TestEntity();

        // Act + Assert
        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    @DisplayName("Two new entities are never equal")
    void givenTwoNewEntities_whenComparingEquality_returnFalse() {
        // Arrange
        TestEntity first = new TestEntity();
        TestEntity second = new TestEntity();

        // Act + Assert
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    @DisplayName("An entity is equal to itself")
    void givenEntity_whenComparingWithItself_returnTrue() {
        // Arrange
        TestEntity entity = new TestEntity();

        // Act + Assert
        assertThat(entity).isEqualTo(entity);
    }

    @Test
    @DisplayName("Two instances with the same id are equal, like a saved entity and its reloaded copy")
    void givenTwoInstancesWithSameId_whenComparingEquality_returnTrue() {
        // Arrange
        TestEntity original = new TestEntity();
        TestEntity reloaded = withId(new TestEntity(), original.getId());

        // Act + Assert
        assertThat(reloaded).isEqualTo(original);
        assertThat(reloaded.hashCode()).isEqualTo(original.hashCode());
    }

    @Test
    @DisplayName("Entities of different types with the same id are not equal")
    void givenDifferentTypesWithSameId_whenComparingEquality_returnFalse() {
        // Arrange
        TestEntity entity = new TestEntity();
        OtherTestEntity other = withId(new OtherTestEntity(), entity.getId());

        // Act + Assert
        assertThat(entity).isNotEqualTo(other);
    }

    @Test
    @DisplayName("An entity is not equal to null")
    void givenEntity_whenComparingWithNull_returnFalse() {
        // Arrange
        TestEntity entity = new TestEntity();

        // Act + Assert
        assertThat(entity).isNotEqualTo(null);
    }

    @Test
    @DisplayName("The hash code does not change when the entity is saved")
    void givenNewEntity_whenSaved_returnSameHashCode() {
        // Arrange
        TestEntity entity = new TestEntity();
        int hashBeforeSave = entity.hashCode();

        // Act: what @PostPersist does after saving
        entity.markNotNew();

        // Assert
        assertThat(entity.hashCode()).isEqualTo(hashBeforeSave);
    }

    @Test
    @DisplayName("A newly created entity is new")
    void givenNewEntity_whenCheckingIsNew_returnTrue() {
        // Arrange + Act
        TestEntity entity = new TestEntity();

        // Assert
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    @DisplayName("After saving or loading, the entity is no longer new")
    void givenSavedOrLoadedEntity_whenCheckingIsNew_returnFalse() {
        // Arrange
        TestEntity entity = new TestEntity();

        // Act: what @PostPersist and @PostLoad do
        entity.markNotNew();

        // Assert
        assertThat(entity.isNew()).isFalse();
    }
}