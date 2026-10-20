package org.switchfully.eurder.eurder.customer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCustomerDTO(
        @Schema(example = "Jane") @NotBlank String firstName,
        @Schema(example = "Smith") @NotBlank String lastName,
        @Schema(example = "jane.smith@example.com") @NotBlank @Email String email,
        @Valid @NotNull AddressDTO address,
        @Schema(example = "+32 470 12 34 56") @NotBlank String phoneNumber
) {
}
