package org.switchfully.eurder.eurder.eurder.api.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOrderDTO(
        @Schema(example = "f47ac10b-58cc-4372-a567-0e02b2c3d479") @NotNull UUID customerId,
        @ArraySchema(minItems = 1) @NotEmpty List<@Valid CreateItemGroupDTO> itemGroups) {
}