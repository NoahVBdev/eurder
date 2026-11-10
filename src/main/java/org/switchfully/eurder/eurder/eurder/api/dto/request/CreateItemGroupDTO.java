package org.switchfully.eurder.eurder.eurder.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateItemGroupDTO(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @NotNull UUID itemId,
        @Schema(example = "2") @Positive int amount) {
}