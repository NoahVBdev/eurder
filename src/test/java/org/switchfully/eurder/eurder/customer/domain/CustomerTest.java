package org.switchfully.eurder.eurder.customer.domain;

import org.apache.commons.validator.routines.EmailValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.switchfully.eurder.eurder.common.domain.Address;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {
    private static final Address ADDRESS = new Address("Rue Neuve", "1", "1000", "Brussels", "Belgium");
    private static final String PLAIN_DUMMY_PASSWORD = "{noop}password";

    private static Customer validCustomer() {
        return new Customer("Jane", "Smith", "jane.smith@example.com", ADDRESS, "+32425447444", PLAIN_DUMMY_PASSWORD);
    }

    @Test
    @DisplayName("new customer given a valid email gets an id")
    void newCustomer_givenValidEmail_getsAnId() {
        // GIVEN
        Customer customer = new Customer("Jane", "Smith", "jane.smith@example.com", ADDRESS, "+32 470 12 34 56", PLAIN_DUMMY_PASSWORD);

        // Then
        assertThat(customer.getId()).isNotNull();
    }

    @Test
    @DisplayName("two customers get different IDs")
    void twoCustomers_getDifferentIds() {
        Customer first = new Customer("Jane", "Smith", "jane.smith@example.com", ADDRESS, "+32 470 12 34 56", PLAIN_DUMMY_PASSWORD);
        Customer second = new Customer("John", "Doe", "john.doe@example.com", ADDRESS, "+32 470 12 34 70", PLAIN_DUMMY_PASSWORD);

        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    @DisplayName("blank first address throws an exception")
    void blankFirstName_throwsException() {
        assertThatThrownBy(() -> new Customer(" ", "Doe", "jane@example.com", ADDRESS, "0470", PLAIN_DUMMY_PASSWORD))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("blank last address throws an exception")
    void blankLastName_throwsException() {
        assertThatThrownBy(() -> new Customer("Jane", " ", "jane@example.com", ADDRESS, "0470", PLAIN_DUMMY_PASSWORD))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("invalid email throws an exception")
    @ValueSource(strings = {"",
            " ",
            "@example.com",
            ".@example.com",
            ".@.com",
            ".@com",
            ".@",
            "@",
            ".smith@example.com",
            "jane.smith.@example.com",
            ".jane.smith@example.com",
            "jane..smith@example.com",
            "jane.smith@.com",
            "jane..smith@.example.com",
            "jane..smith@example.",
            "jane.smith@example..com",
            "jane.smithexample.com.",
            "janesmithexamplecom"
    })
    void invalidEmail_throwsException(String email) {
        assertThat(EmailValidator.getInstance().isValid(email)).isFalse();
    }

    @Test
    @DisplayName("invalid email throws an exception")
    void mixedCaseEmail_returnsEmailInLowerCase() {
        Customer customer = new Customer("Jane", "Smith", "jaNe.sMIth@examPle.coM", ADDRESS, "+32 470 12 34 56", PLAIN_DUMMY_PASSWORD);

        assertThat(customer.getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Nested
    @DisplayName("Email normalisation")
    class EmailNormalisation {

        @Test
        @DisplayName("constructor stores email in lowercase")
        void constructor_storesEmailInLowercase() {
            Customer customer = new Customer("Jane", "Smith", "Jane.Smith@Example.COM", ADDRESS, "+32425447444", "hash");

            assertThat(customer.getEmail()).isEqualTo("jane.smith@example.com");
        }

        @Test
        @DisplayName("normalizeEmail is independent of the default locale")
        void normalizeEmail_isIndependentOfDefaultLocale() {
            Locale original = Locale.getDefault();
            try {
                // In Turkish, "I".toLowerCase() becomes a dotless "ı"
                Locale.setDefault(Locale.forLanguageTag("tr-TR"));

                assertThat(Customer.normalizeEmail("ADMIN@EURDER.COM")).isEqualTo("admin@eurder.com");
            } finally {
                Locale.setDefault(original);
            }
        }
    }

    @Nested
    @DisplayName("Changing a customer")
    class ChangingACustomer {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t"})
        @DisplayName("setFirstName rejects a blank value and keeps the old one")
        void setFirstName_rejectsBlank(String value) {
            Customer customer = validCustomer();

            assertThatThrownBy(() -> customer.setFirstName(value)).isInstanceOf(IllegalArgumentException.class);
            assertThat(customer.getFirstName()).isEqualTo("Jane");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t"})
        @DisplayName("setLastName rejects a blank value and keeps the old one")
        void setLastName_rejectsBlank(String value) {
            Customer customer = validCustomer();

            assertThatThrownBy(() -> customer.setLastName(value)).isInstanceOf(IllegalArgumentException.class);
            assertThat(customer.getLastName()).isEqualTo("Smith");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t"})
        @DisplayName("setPhoneNumber rejects a blank value and keeps the old one")
        void setPhoneNumber_rejectsBlank(String value) {
            Customer customer = validCustomer();

            assertThatThrownBy(() -> customer.setPhoneNumber(value)).isInstanceOf(IllegalArgumentException.class);
            assertThat(customer.getPhoneNumber()).isEqualTo("+32425447444");
        }

        @Test
        @DisplayName("changeAddress rejects null and keeps the old address")
        void changeAddress_rejectsNull() {
            Customer customer = validCustomer();

            assertThatThrownBy(() -> customer.changeAddress(null)).isInstanceOf(IllegalArgumentException.class);
            assertThat(customer.getAddress()).isEqualTo(ADDRESS);
        }
    }
}

