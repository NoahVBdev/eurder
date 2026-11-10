package org.switchfully.eurder.eurder.eurder.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ItemGroupDTO(UUID id,
                           UUID itemId,
                           String itemName,
                           int amount,
                           BigDecimal pricePerUnit,
                           BigDecimal totalPrice,
                           LocalDate shippingDate) {
}