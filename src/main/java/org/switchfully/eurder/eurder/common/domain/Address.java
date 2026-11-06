package org.switchfully.eurder.eurder.common.domain;

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
