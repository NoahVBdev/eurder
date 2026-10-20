package org.switchfully.eurder.eurder.customer.api.dto;

import org.switchfully.eurder.eurder.customer.domain.Customer;

import java.util.UUID;

public record CustomerDTO(UUID id, String firstName, String lastName, String email, AddressDTO address,
                          String phoneNumber) {
}
