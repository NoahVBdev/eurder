package org.switchfully.eurder.eurder.customer.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.data.annotation.Version;
import org.switchfully.eurder.eurder.common.domain.Address;
import org.switchfully.eurder.eurder.common.domain.BaseEntity;

import java.util.Locale;

@Entity
@Table(name = "customer")
public class Customer extends BaseEntity {

    private String firstName;
    private String lastName;
    private String email;
    @Embedded
    private Address address;
    private String phoneNumber;
    private String passwordHash;

    public Customer(String firstName, String lastName, String email, Address address, String phoneNumber, String passwordHash) {
        validateFirstName(firstName);
        validateLastName(lastName);
        validateEmail(email);
        validateAddress(address);
        validatePhoneNumber(phoneNumber);
        validatePasswordHash(passwordHash);

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = normalizeEmail(email);
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
    }

    protected Customer() {
    }

    // Getters & Setters

    //    Validators
    private static void validateFirstName(String firstName) {
        requireNotBlank(firstName, "First name is required");
    }

    private static void validateLastName(String lastName) {
        requireNotBlank(lastName, "Last name is required");
    }

    private static void validateEmail(String email) {
        if (email == null || !EmailValidator.getInstance().isValid(email))
            throw new IllegalArgumentException("Valid email is required");
    }

    private static void validateAddress(Address address) {
        if (address == null) throw new IllegalArgumentException("Address is required");
    }

    private static void validatePhoneNumber(String phoneNumber) {
        requireNotBlank(phoneNumber, "Phone number is required");
    }

    private static void validatePasswordHash(String passwordHash) {
        requireNotBlank(passwordHash, "Password is required");
    }

    private static void requireNotBlank(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
    }

    public static String normalizeEmail(String email) {
        return email.toLowerCase(Locale.ROOT);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        validateFirstName(firstName);
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        validateLastName(lastName);
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void changeAddress(Address newAddress) {
        validateAddress(newAddress);
        this.address = newAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        this.phoneNumber = phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}

