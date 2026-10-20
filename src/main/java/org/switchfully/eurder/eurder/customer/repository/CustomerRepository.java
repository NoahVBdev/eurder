package org.switchfully.eurder.eurder.customer.repository;

import jakarta.validation.constraints.NotNull;
import org.switchfully.eurder.eurder.customer.domain.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    boolean existsByEmail(String email);
    Optional<Customer> findById(@NotNull UUID id);
}
