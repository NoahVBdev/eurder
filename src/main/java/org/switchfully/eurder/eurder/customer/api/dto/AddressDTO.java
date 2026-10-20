package org.switchfully.eurder.eurder.customer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AddressDTO(
        @Schema(example = "Rue Neuve") @NotBlank String street,
        @Schema(example = "1") @NotBlank String houseNumber,
        @Schema(example = "1000") @NotBlank String postalCode,
        @Schema(example = "Brussels") @NotBlank String city,
        @Schema(example = "Belgium") @NotBlank String country) {
}
