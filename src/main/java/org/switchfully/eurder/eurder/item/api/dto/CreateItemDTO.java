package org.switchfully.eurder.eurder.item.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateItemDTO(@Schema(example = "Fridge Coolio Z") @NotBlank String name,
                            @Schema(example = "Energy-efficient fridge-freezer, 300 litres") @NotBlank String description,
                            @Schema(example = "749.95") @NotNull @Positive @Digits(integer = 9, fraction = 2) BigDecimal price,
                            @Schema(example = "12") @NotNull @PositiveOrZero Integer stock) {
}
