package org.switchfully.eurder.eurder.customer.domain;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;
import java.util.UUID;

public class Customer {
    private final UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private Address address;
    private String phoneNumber;

    public Customer(String firstName, String lastName, String email, Address address, String phoneNumber) {
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("First name is required");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("Last name is required");
        if (email == null || !EmailValidator.getInstance().isValid(email))
            throw new IllegalArgumentException("Valid email is required");
        if (address == null) throw new IllegalArgumentException("Address is required");
        if (phoneNumber == null || phoneNumber.isBlank())
            throw new IllegalArgumentException("Phone number is required");

        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void changeAddress(Address newAddress) {
        if (newAddress == null) throw new IllegalArgumentException("Address is required");
        this.address = newAddress;
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

    public record Address(String street, String houseNumber, String postalCode, String city, String country) {
        public Address {
            if (street == null || street.isBlank()) throw new IllegalArgumentException("Street is required");
            if (houseNumber == null || houseNumber.isBlank())
                throw new IllegalArgumentException("House number is required");
            if (postalCode == null || postalCode.isBlank())
                throw new IllegalArgumentException("Postal code is required");
            if (city == null || city.isBlank()) throw new IllegalArgumentException("City is required");
            if (country == null || country.isBlank()) throw new IllegalArgumentException("Country is required");
        }
    }
}

