package org.switchfully.eurder.eurder.eurder.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOrderDTO(@NotNull UUID customerId, @NotNull List<CreateItemGroupDTO> itemGroups) {
}