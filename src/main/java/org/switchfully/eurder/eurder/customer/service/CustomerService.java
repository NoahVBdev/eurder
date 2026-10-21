package org.switchfully.eurder.eurder.customer.service;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.switchfully.eurder.eurder.common.exception.ResourceNotFoundException;
import org.switchfully.eurder.eurder.customer.api.dto.CreateCustomerDTO;
import org.switchfully.eurder.eurder.customer.api.dto.CustomerDTO;
import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.customer.exception.CustomerNotFoundException;
import org.switchfully.eurder.eurder.customer.exception.EmailAlreadyInUseException;
import org.switchfully.eurder.eurder.customer.repository.InMemoryCustomerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final InMemoryCustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(InMemoryCustomerRepository customerRepository, CustomerMapper customerMapper, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomerDTO createCustomer(@Valid CreateCustomerDTO createCustomerDTO) {
        if (customerRepository.existsByEmail(createCustomerDTO.email())) {
            throw new EmailAlreadyInUseException(createCustomerDTO.email());
        }
        String passwordHash = passwordEncoder.encode(createCustomerDTO.password());  // Hash password before saving
        Customer saved = customerRepository.save(customerMapper.toDomain(createCustomerDTO, passwordHash));
        return customerMapper.toDTO(saved);
    }

    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.getAll();
        if (customers.isEmpty()) {
            log.debug("No customers found");
            throw new ResourceNotFoundException("No customers found");
        }
        return customerMapper.toDTO(customers);
    }

    public CustomerDTO findCustomerById(UUID id) throws CustomerNotFoundException {
         Optional<Customer> customer = customerRepository.findById(id);
         if (customer.isEmpty()) {
             log.debug("Could not find customer with id: " + id.toString());
             throw new CustomerNotFoundException(id.toString()) ;
         }
        return customerMapper.toDTO(customer.get());
    }

    public CustomerDTO findCustomerByEmail(String customerEmail) {
        Optional<Customer> customer = customerRepository.findByEmail(customerEmail);
        if (customer.isEmpty()) {
            log.debug("Could not find customer with email: " + customerEmail);
            throw new CustomerNotFoundException(customerEmail) ;
        }
        return customerMapper.toDTO(customer.get());
    }
}
