package org.switchfully.eurder.eurder.customer.service;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.switchfully.eurder.eurder.common.domain.Address;
import org.switchfully.eurder.eurder.customer.api.dto.CreateCustomerDTO;
import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.customer.exception.EmailAlreadyInUseException;
import org.switchfully.eurder.eurder.customer.repository.CustomerRepository;

import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private static final Address ADDRESS = new Address("Rue Neuve", "100B", "1000", "Brussels", "Belgium");

    private final CreateCustomerDTO createCustomerDTO = mock(CreateCustomerDTO.class);
    @Mock
    CustomerRepository customerRepository;
    @Mock
    CustomerMapper customerMapper;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    CustomerService customerService;

    private static DataIntegrityViolationException violationOf(String constraintName) {
        var hibernateCause = new ConstraintViolationException("violation", new SQLException("violation"), constraintName);
        return new DataIntegrityViolationException("could not execute statement", hibernateCause);
    }

    private void givenSaveThrows(DataIntegrityViolationException exception) {
        Customer customer = new Customer("Jane", "Smith", "jane.smith@example.com", ADDRESS, "+32425447444", "hash");
        when(createCustomerDTO.email()).thenReturn("jane.smith@example.com");
        when(createCustomerDTO.password()).thenReturn("secret-password");
        when(passwordEncoder.encode("secret-password")).thenReturn("hash");
        when(customerMapper.toDomain(createCustomerDTO, "hash")).thenReturn(customer);
        when(customerRepository.saveAndFlush(customer)).thenThrow(exception);
    }

    @Test
    void createCustomer_existingEmailWithDifferentCase_isRejectedBeforeSaving() {
        when(createCustomerDTO.email()).thenReturn("Jane.Smith@Example.COM");
        when(customerRepository.existsByEmail("jane.smith@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(createCustomerDTO))
                .isInstanceOf(EmailAlreadyInUseException.class);
        verify(customerRepository, never()).saveAndFlush(any());
    }

    @Test
    void createCustomer_uniqueEmailConstraintViolated_throwsEmailAlreadyInUse() {
        givenSaveThrows(violationOf("uq_customer_email"));

        assertThatThrownBy(() -> customerService.createCustomer(createCustomerDTO))
                .isInstanceOf(EmailAlreadyInUseException.class);
    }

    @Test
    void createCustomer_constraintNameInUppercase_isStillRecognised() {
        givenSaveThrows(violationOf("UQ_CUSTOMER_EMAIL"));

        assertThatThrownBy(() -> customerService.createCustomer(createCustomerDTO))
                .isInstanceOf(EmailAlreadyInUseException.class);
    }

    @Test
    void createCustomer_otherConstraintViolated_rethrowsOriginalException() {
        DataIntegrityViolationException original = violationOf("ck_some_other_constraint");
        givenSaveThrows(original);

        assertThatThrownBy(() -> customerService.createCustomer(createCustomerDTO)).isSameAs(original);
    }

    @Test
    void createCustomer_constraintNameUnknown_rethrowsOriginalException() {
        DataIntegrityViolationException original = violationOf(null);
        givenSaveThrows(original);

        assertThatThrownBy(() -> customerService.createCustomer(createCustomerDTO)).isSameAs(original);
    }

}