package org.switchfully.eurder.eurder.customer.repository;

import org.switchfully.eurder.eurder.customer.domain.Customer;

public interface CustomerRepository {
    Customer save(Customer customer);
    boolean existsByEmail(String email);
}
