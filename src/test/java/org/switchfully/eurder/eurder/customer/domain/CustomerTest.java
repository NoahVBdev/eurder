package org.switchfully.eurder.eurder.customer.domain;

import org.apache.commons.validator.routines.EmailValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.switchfully.eurder.eurder.common.domain.Address;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {
    private final String PLAIN_DUMMY_PASSWORD = "{noop}password";
    private final Address address = new Address("Rue Neuve", "1", "1000", "Brussels", "Belgium");

    @Test
    @DisplayName("new customer given a valid email gets an id")
    void newCustomer_givenValidEmail_getsAnId() {
        // GIVEN
        Customer customer = new Customer("Jane", "Smith", "jane.smith@example.com", address, "+32 470 12 34 56", PLAIN_DUMMY_PASSWORD);

        // Then
        assertThat(customer.getId()).isNotNull();
    }

    @Test
    @DisplayName("two customers get different IDs")
    void twoCustomers_getDifferentIds() {
        Customer first = new Customer("Jane", "Smith", "jane.smith@example.com", address, "+32 470 12 34 56", PLAIN_DUMMY_PASSWORD);
        Customer second = new Customer("John", "Doe", "john.doe@example.com", address, "+32 470 12 34 70", PLAIN_DUMMY_PASSWORD);

        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    @DisplayName("blank first address throws an exception")
    void blankFirstName_throwsException() {
        assertThatThrownBy(() -> new Customer(" ", "Doe", "jane@example.com", address, "0470", PLAIN_DUMMY_PASSWORD))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("blank last address throws an exception")
    void blankLastName_throwsException() {
        assertThatThrownBy(() -> new Customer("Jane", " ", "jane@example.com", address, "0470", PLAIN_DUMMY_PASSWORD))
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
        Customer customer = new Customer("Jane", "Smith", "jaNe.sMIth@examPle.coM", address, "+32 470 12 34 56", PLAIN_DUMMY_PASSWORD);

        assertThat(customer.getEmail()).isEqualTo("jane.smith@example.com");
    }

}

