package org.switchfully.eurder.eurder;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.switchfully.eurder.eurder.helper.IntegrationTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@IntegrationTest
@Transactional
class DatabaseSchemaIntegrationTest {

    private static final String INSERT_ITEM_WITH_STOCK = """
            INSERT INTO eurder.item (id, name, description, price, stock)
            VALUES (?, 'Fridge', 'Cold', 10.00, ?)
            """;

    private static final String INSERT_CUSTOMER = """
            INSERT INTO eurder.customer (id, first_name, last_name, email, password_hash, phone_number,
                                         street, house_number, postal_code, city, country)
            VALUES (?, 'John', 'Doe', 'john.doe@example.com', 'not-a-real-hash', '+32425447444',
                    'Rue Neuve', '100B', '1000', 'Brussels', 'Belgium')
            """;

    private static final String INSERT_ITEM_GROUP = """
            INSERT INTO eurder.item_group (id, eurder_id, item_id, item_name, price_per_unit, amount, shipping_date)
            VALUES (?, ?, ?, 'Fridge', 10.00, 1, DATE '2026-11-07')
            """;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Flyway flyway;

    @Test
    @DisplayName("Flyway has applied version 1 and has no pending migrations")
    void flyway_hasAppliedVersion1_andHasNoPendingMigrations() {
        assertThat(flyway.info().applied())
                .extracting(MigrationInfo::getVersion)
                .contains(MigrationVersion.fromVersion("1"));
        assertThat(flyway.info().pending()).isEmpty();
    }

    @Test
    @DisplayName("Item with negative stock is rejected by database")
    void itemWithNegativeStock_isRejectedByDatabase() {
        assertThatThrownBy(() -> jdbcTemplate.update(INSERT_ITEM_WITH_STOCK, UUID.randomUUID(), -1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Customer with duplicate email is rejected by database")
    void customerWithDuplicateEmail_isRejectedByDatabase() {
        jdbcTemplate.update(INSERT_CUSTOMER, UUID.randomUUID());

        assertThatThrownBy(() -> jdbcTemplate.update(INSERT_CUSTOMER, UUID.randomUUID()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Item group with unknown order is rejected by database")
    void itemGroupWithUnknownEurder_isRejectedByDatabase() {
        UUID existingItemId = UUID.randomUUID();
        jdbcTemplate.update(INSERT_ITEM_WITH_STOCK, existingItemId, 5);
        UUID unknownEurderId = UUID.randomUUID();

        assertThatThrownBy(() -> jdbcTemplate.update(INSERT_ITEM_GROUP, UUID.randomUUID(), unknownEurderId, existingItemId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
