package org.switchfully.eurder.eurder.item.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class ItemTest {

    private static final String NAME = "Fridge Coolio Z";
    private static final String DESCRIPTION = "Energy-efficient fridge-freezer, 300 litres";
    private static final BigDecimal PRICE = new BigDecimal("749.95");
    private static final Integer AMOUNT = 12;

    @Test
    @DisplayName("with valid data creates an item with a generated id")
    void givenValidData_createsItem() {
        Item item = new Item(NAME, DESCRIPTION, PRICE, AMOUNT);

        assertThat(item.getId()).isNotNull();
        assertThat(item.getName()).isEqualTo(NAME);
        assertThat(item.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(item.getPrice()).isEqualByComparingTo(PRICE);
        assertThat(item.getAmount()).isEqualTo(AMOUNT);
    }

    @Test
    @DisplayName("two items with the same data get different ids")
    void givenSameData_generatesDifferentIds() {
        Item first = new Item(NAME, DESCRIPTION, PRICE, AMOUNT);
        Item second = new Item(NAME, DESCRIPTION, PRICE, AMOUNT);

        assertThat(first.getId()).isNotEqualTo(second.getId());
        assertThat(first).isNotEqualTo(second);
    }

    @Nested
    @DisplayName("Name")
    class Name {
        @ParameterizedTest(name = "[{index}] name = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("null, empty or blank is rejected")
        void givenBlankName_throws(String name) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Item(name, DESCRIPTION, PRICE, AMOUNT))
                    .withMessageContaining("Name");
        }
    }

    @Nested
    @DisplayName("Description")
    class Description {
        @ParameterizedTest(name = "[{index}] description = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("null, empty or blank is rejected")
        void givenBlankDescription_throws(String description) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Item(NAME, description, PRICE, AMOUNT))
                    .withMessageContaining("Description");
        }
    }

    @Nested
    @DisplayName("Price")
    class Price {
        @Test
        @DisplayName("null is rejected")
        void givenNullPrice_throws() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Item(NAME, DESCRIPTION, null, AMOUNT))
                    .withMessageContaining("Price");
        }

        @ParameterizedTest(name = "[{index}] price = {0}")
        @ValueSource(strings = {"-0.01", "-749.95", "0", "0.00"})
        @DisplayName("zero or negative is rejected")
        void givenZeroOrNegativePrice_throws(String price) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Item(NAME, DESCRIPTION, new BigDecimal(price), AMOUNT))
                    .withMessageContaining("Price");
        }

        @Test
        @DisplayName("more than two decimals is rejected")
        void givenMoreThanTwoDecimals_throws() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Item(NAME, DESCRIPTION, new BigDecimal("749.955"), AMOUNT))
                    .withMessageContaining("Price");
        }

        @Test
        @DisplayName("smallest positive price is accepted")
        void givenOneCent_createsItem() {
            Item item = new Item(NAME, DESCRIPTION, new BigDecimal("0.01"), AMOUNT);

            assertThat(item.getPrice()).isEqualByComparingTo("0.01");
        }
    }

    @Nested
    @DisplayName("Amount")
    class Amount {
        @ParameterizedTest(name = "[{index}] amount = {0}")
        @ValueSource(ints = {-1, -12, Integer.MIN_VALUE})
        @DisplayName("negative is rejected")
        void givenNegativeAmount_throws(int amount) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Item(NAME, DESCRIPTION, PRICE, amount))
                    .withMessageContaining("Amount");
        }

        @Test
        @DisplayName("zero is accepted (out of stock)")
        void givenZeroAmount_createsItem() {
            Item item = new Item(NAME, DESCRIPTION, PRICE, 0);

            assertThat(item.getAmount()).isZero();
        }
    }
}