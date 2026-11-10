package org.switchfully.eurder.eurder.item.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemDTO(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        int stock) {
}
