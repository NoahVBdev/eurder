package org.switchfully.eurder.eurder.security.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.switchfully.eurder.eurder.security.exception.ForbiddenException;
import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.customer.repository.InMemoryCustomerRepository;
import org.switchfully.eurder.eurder.security.TestAuth;
import org.switchfully.eurder.eurder.security.domain.AuthenticatedUser;
import org.switchfully.eurder.eurder.security.domain.Feature;
import org.switchfully.eurder.eurder.security.domain.Role;
import org.switchfully.eurder.eurder.security.exception.UnauthorizedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SecurityServiceTest {
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
    private final SecurityService securityService =
            new SecurityService(customerRepository, passwordEncoder, "admin@eurder.com", "admin-pw");

    @BeforeEach
    void setUp() {
        customerRepository.save(new Customer("Jane", "Smith", "jane@example.com",
                new Customer.Address("Rue Neuve", "1", "1000", "Brussels", "Belgium"),
                "0470", passwordEncoder.encode("jane-pw")));
    }

    @Test
    void customer_validPassword_isAllowed() {
        AuthenticatedUser user = securityService.validateAuthorization(TestAuth.basic("jane@example.com", "jane-pw"), Feature.PLACE_ORDER);
        assertThat(user).isEqualTo(new AuthenticatedUser("jane@example.com", Role.CUSTOMER));
    }

    @Test
    void customer_adminFeature_isForbidden() {
        assertThatThrownBy(() -> securityService.validateAuthorization(TestAuth.basic("jane@example.com", "jane-pw"), Feature.ADD_ITEM))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void wrongPassword_isUnauthorized() {
        assertThatThrownBy(() -> securityService.validateAuthorization(TestAuth.basic("jane@example.com", "nope"), Feature.PLACE_ORDER))
                .isInstanceOf(UnauthorizedException.class);
    }
}