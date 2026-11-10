package org.switchfully.eurder.eurder.customer.service;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.switchfully.eurder.eurder.common.exception.ResourceNotFoundException;
import org.switchfully.eurder.eurder.customer.api.dto.CreateCustomerDTO;
import org.switchfully.eurder.eurder.customer.api.dto.CustomerDTO;
import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.customer.exception.CustomerNotFoundException;
import org.switchfully.eurder.eurder.customer.exception.EmailAlreadyInUseException;
import org.switchfully.eurder.eurder.customer.repository.CustomerRepository;

import java.util.*;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private static final String UNIQUE_EMAIL_CONSTRAINT = "uq_customer_email";

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomerDTO createCustomer(CreateCustomerDTO createCustomerDTO) {
        if (customerRepository.existsByEmail(Customer.normalizeEmail(createCustomerDTO.email()))) {
            throw new EmailAlreadyInUseException(createCustomerDTO.email());
        }

        String passwordHash = passwordEncoder.encode(createCustomerDTO.password());
        Customer saved;
        try {
            saved = customerRepository.saveAndFlush(customerMapper.toDomain(createCustomerDTO, passwordHash));
        } catch (DataIntegrityViolationException e) {
            if (isUniqueEmailViolation(e)) {
                log.warn("Email must be unique constraint violation {}", e.toString());
                throw new EmailAlreadyInUseException(createCustomerDTO.email());
            }
            throw e;
        }

        return customerMapper.toDTO(saved);
    }

    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            log.debug("No customers found");
            throw new ResourceNotFoundException("No customers found");
        }
        return customerMapper.toDTO(customers);
    }

    public CustomerDTO findCustomerById(UUID id) throws CustomerNotFoundException {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) {
            log.debug("Could not find customer with id: {}", id);
            throw new CustomerNotFoundException(id.toString());
        }
        return customerMapper.toDTO(customer.get());
    }

    public CustomerDTO findCustomerByEmail(String customerEmail) {
        Optional<Customer> customer = customerRepository.findByEmail(Customer.normalizeEmail(customerEmail));
        if (customer.isEmpty()) {
            log.debug("Could not find customer with email: {}", customerEmail);
            throw new CustomerNotFoundException(customerEmail);
        }
        return customerMapper.toDTO(customer.get());
    }

    private boolean isUniqueEmailViolation(DataIntegrityViolationException e) {
        return e.getCause() instanceof ConstraintViolationException cve
                && cve.getConstraintName() != null
                && cve.getConstraintName().toLowerCase(Locale.ROOT).contains(UNIQUE_EMAIL_CONSTRAINT);
    }

    public CustomerOrderingDetails getOrderingDetails(String customerEmail) {
        Customer customer = customerRepository.findByEmail(Customer.normalizeEmail(customerEmail))
                .orElseThrow(() -> {
                    log.debug("Could not find customer with email: {}", customerEmail);
                    return new CustomerNotFoundException(customerEmail);
                });
        return new CustomerOrderingDetails(customer.getId(), customer.getAddress());
    }
}
