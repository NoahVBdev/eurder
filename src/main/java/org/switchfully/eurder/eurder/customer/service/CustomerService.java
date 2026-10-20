package org.switchfully.eurder.eurder.customer.service;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.switchfully.eurder.eurder.customer.api.dto.CreateCustomerDTO;
import org.switchfully.eurder.eurder.customer.api.dto.CustomerDTO;
import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.customer.exception.EmailAlreadyInUseException;
import org.switchfully.eurder.eurder.customer.repository.InMemoryCustomerRepository;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final InMemoryCustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(InMemoryCustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public CustomerDTO createCustomer(@Valid CreateCustomerDTO createCustomerDTO) {
        if (customerRepository.existsByEmail(createCustomerDTO.email())) {
            throw new EmailAlreadyInUseException(createCustomerDTO.email());
        }
        Customer saved = customerRepository.save(customerMapper.toDomain(createCustomerDTO));
        return customerMapper.toDTO(saved);
    }
}
