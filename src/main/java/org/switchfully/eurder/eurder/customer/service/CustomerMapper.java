package org.switchfully.eurder.eurder.customer.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.switchfully.eurder.eurder.common.domain.Address;
import org.switchfully.eurder.eurder.customer.api.dto.AddressDTO;
import org.switchfully.eurder.eurder.customer.api.dto.CreateCustomerDTO;
import org.switchfully.eurder.eurder.customer.api.dto.CustomerDTO;
import org.switchfully.eurder.eurder.customer.domain.Customer;

import java.util.List;

@Component
public class CustomerMapper {
    //----------TO DOMAIN----------
    public Customer toDomain(@Valid CreateCustomerDTO createCustomerDTO, String passwordHash) {
        return new Customer(createCustomerDTO.firstName(), createCustomerDTO.lastName(), createCustomerDTO.email(), this.toDomain(createCustomerDTO.address()), createCustomerDTO.phoneNumber(), passwordHash);
    }

    public Address toDomain(@Valid AddressDTO addressDTO) {
        return new Address(addressDTO.street(), addressDTO.houseNumber(), addressDTO.postalCode(), addressDTO.city(), addressDTO.country());
    }

    //----------TO DTO----------
    public CustomerDTO toDTO(Customer saved) {
        return new CustomerDTO(saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getEmail(), this.toDTO(saved.getAddress()), saved.getPhoneNumber());
    }

    public AddressDTO toDTO(Address address) {
        return new AddressDTO(address.street(), address.houseNumber(), address.postalCode(), address.city(), address.country());
    }

    public List<CustomerDTO> toDTO(List<Customer> customers) {
        return customers.stream().map(this::toDTO).toList();
    }
}
