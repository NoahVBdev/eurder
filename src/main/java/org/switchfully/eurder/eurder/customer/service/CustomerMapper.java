package org.switchfully.eurder.eurder.customer.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.switchfully.eurder.eurder.customer.api.dto.AddressDTO;
import org.switchfully.eurder.eurder.customer.api.dto.CreateCustomerDTO;
import org.switchfully.eurder.eurder.customer.api.dto.CustomerDTO;
import org.switchfully.eurder.eurder.customer.domain.Customer;

@Component
public class CustomerMapper {
    public Customer toDomain(@Valid CreateCustomerDTO createCustomerDTO) {
        return new Customer(createCustomerDTO.firstName(), createCustomerDTO.lastName(), createCustomerDTO.email(), this.toDomain(createCustomerDTO.address()), createCustomerDTO.phoneNumber());
    }

    public Customer.Address toDomain(AddressDTO addressDTO) {
        return new Customer.Address(addressDTO.street(), addressDTO.houseNumber(), addressDTO.postalCode(), addressDTO.city(), addressDTO.country());
    }

    public CustomerDTO toDTO(Customer saved) {
        return new CustomerDTO(saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getEmail(), this.toDTO(saved.getAddress()), saved.getPhoneNumber());
    }

    public AddressDTO toDTO(Customer.Address address) {
        return new AddressDTO(address.street(), address.houseNumber(), address.postalCode(), address.city(), address.country());
    }

}
