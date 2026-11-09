package org.switchfully.eurder.eurder.customer.domain;

import jakarta.persistence.*;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.data.domain.Persistable;
import org.switchfully.eurder.eurder.common.domain.Address;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "customer")
public class Customer implements Persistable<UUID> {

    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    @Embedded
    private Address address;
    private String phoneNumber;
    private String passwordHash;
    @Transient
    private boolean isNew = true;

    public Customer(String firstName, String lastName, String email, Address address, String phoneNumber, String passwordHash) {
        validateFirstName(firstName);
        validateLastName(lastName);
        validateEmail(email);
        validateAddress(address);
        validatePhoneNumber(phoneNumber);
        validatePasswordHash(passwordHash);

        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = normalizeEmail(email);
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
    }

    protected Customer() {
    }

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

    @Override
    public UUID getId() {
        return id;
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

    /**
     * Returns if the {@code Persistable} is new or was persisted already.
     *
     * @return if {@literal true} the object is new.
     */
    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @PostPersist
    @PostLoad
    protected void markAsNotNew() {
        this.isNew = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}

