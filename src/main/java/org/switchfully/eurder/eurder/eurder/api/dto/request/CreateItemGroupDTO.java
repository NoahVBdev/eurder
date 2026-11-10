package org.switchfully.eurder.eurder.eurder.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateItemGroupDTO(
        @NotNull UUID itemId,
        @NotNull @Positive int amount) {
}