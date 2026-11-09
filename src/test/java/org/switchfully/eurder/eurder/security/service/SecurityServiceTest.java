package org.switchfully.eurder.eurder.security.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.switchfully.eurder.eurder.common.domain.Address;
import org.switchfully.eurder.eurder.security.exception.ForbiddenException;
import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.customer.repository.CustomerRepository;
import org.switchfully.eurder.eurder.security.TestAuth;
import org.switchfully.eurder.eurder.security.domain.AuthenticatedUser;
import org.switchfully.eurder.eurder.security.domain.Feature;
import org.switchfully.eurder.eurder.security.domain.Role;
import org.switchfully.eurder.eurder.security.exception.UnauthorizedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Mock
    CustomerRepository customerRepository;

    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService(customerRepository, passwordEncoder, "admin@eurder.com", "admin-pw");
    }

    private void givenJaneExists() {
        Customer jane = new Customer("Jane", "Smith", "jane@example.com",
                new Address("Rue Neuve", "1", "1000", "Brussels", "Belgium"),
                "0470", passwordEncoder.encode("jane-pw"));

        when(customerRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(jane));
    }

    @Test
    void customer_validPassword_isAllowed() {
        givenJaneExists();

        AuthenticatedUser user = securityService.validateAuthorization(TestAuth.basic("jane@example.com", "jane-pw"), Feature.PLACE_ORDER);
        assertThat(user).isEqualTo(new AuthenticatedUser("jane@example.com", Role.CUSTOMER));
    }

    @Test
    void customer_emailInDifferentCase_isLookedUpInLowercase() {
        givenJaneExists();

        AuthenticatedUser user = securityService.validateAuthorization(TestAuth.basic("Jane@Example.COM", "jane-pw"), Feature.PLACE_ORDER);

        assertThat(user.role()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void customer_adminFeature_isForbidden() {
        givenJaneExists();

        assertThatThrownBy(() -> securityService.validateAuthorization(TestAuth.basic("jane@example.com", "jane-pw"), Feature.ADD_ITEM))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void wrongPassword_isUnauthorized() {
        givenJaneExists();

        assertThatThrownBy(() -> securityService.validateAuthorization(TestAuth.basic("jane@example.com", "nope"), Feature.PLACE_ORDER))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void unknownEmail_isUnauthorized() {
        assertThatThrownBy(() -> securityService.validateAuthorization(TestAuth.basic("nobody@example.com", "whatever"), Feature.PLACE_ORDER))
                .isInstanceOf(UnauthorizedException.class);
    }
}