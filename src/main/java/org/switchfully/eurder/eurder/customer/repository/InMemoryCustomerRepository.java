package org.switchfully.eurder.eurder.customer.repository;

import org.springframework.stereotype.Repository;
import org.switchfully.eurder.eurder.customer.domain.Customer;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the in-memory repository for {@link Customer} objects.
 * <p>
 * Customers are stored in a map with their UUID as the key. The repository starts
 * with a few hardcoded customers so the application has data to work with.
 * </p>
 */
@Repository
public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<UUID, Customer> customers = new ConcurrentHashMap<>();

    /**
     * Save a customer in the repository
     *
     * <p>The customer is stored using its UUID as the key. If a customer with the same UUID
     * already exists, it is replaced by the provided customer.</p>
     *
     * @param customer customer to save in repository
     * @return saved {@code Customer} on success
     */
    @Override
    public Customer save(Customer customer) {
        customers.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public boolean existsByEmail(String email) {
        return customers.values().stream().anyMatch(customer -> customer.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return Optional.ofNullable(customers.get(id));
    }

    /**
     * <b>ONLY FOR TESTING PURPOSES</b>
     */
    public void deleteAll() {
        customers.clear();
    }
}
