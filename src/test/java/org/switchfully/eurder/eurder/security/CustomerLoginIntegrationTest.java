package org.switchfully.eurder.eurder.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.switchfully.eurder.eurder.common.domain.Address;
import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.customer.repository.CustomerRepository;
import org.switchfully.eurder.eurder.helper.IntegrationTest;
import org.switchfully.eurder.eurder.security.domain.AuthenticatedUser;
import org.switchfully.eurder.eurder.security.domain.Feature;
import org.switchfully.eurder.eurder.security.domain.Role;
import org.switchfully.eurder.eurder.security.service.SecurityService;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class CustomerLoginIntegrationTest {

    @Autowired
    SecurityService securityService;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void customerCanLogIn_withEmailInDifferentCase() {
        Address address = new Address("Rue Neuve", "100B", "1000", "Brussels", "Belgium");
        customerRepository.save(new Customer("Jane", "Smith", "jane.smith@example.com", address,
                "+32425447444", passwordEncoder.encode("secret-password")));

        AuthenticatedUser user = securityService.validateAuthorization(
                TestAuth.basic("Jane.Smith@Example.COM", "secret-password"), Feature.PLACE_ORDER);

        assertThat(user.role()).isEqualTo(Role.CUSTOMER);
    }
}