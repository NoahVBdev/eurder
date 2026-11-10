package org.switchfully.eurder.eurder.eurder.api.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderDTO(
        UUID id,
        UUID customerId,
        List<ItemGroupDTO> itemGroups,
        BigDecimal totalPrice) {
}
