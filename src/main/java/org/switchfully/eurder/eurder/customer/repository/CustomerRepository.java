package org.switchfully.eurder.eurder.customer.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.switchfully.eurder.eurder.customer.domain.Customer;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByEmail(@NotBlank @Email String email);
    Optional<Customer> findByEmail(@NotNull @Email String email);
}
