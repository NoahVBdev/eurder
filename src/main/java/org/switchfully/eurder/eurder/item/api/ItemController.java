package org.switchfully.eurder.eurder.item.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.switchfully.eurder.eurder.item.api.dto.CreateItemDTO;
import org.switchfully.eurder.eurder.item.api.dto.ItemDTO;
import org.switchfully.eurder.eurder.item.service.ItemService;
import org.switchfully.eurder.eurder.security.domain.Feature;
import org.switchfully.eurder.eurder.security.service.SecurityService;


@RestController
@RequestMapping("/items")
@Tag(name = "Items")
public class ItemController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    
    private final ItemService itemService;
    private final SecurityService securityService;

    public ItemController(ItemService itemService, SecurityService securityService) {
        this.itemService = itemService;
        this.securityService = securityService;
    }

    @Operation(summary = "Add an item as an admin", description = "Story 2")
    @ApiResponse(responseCode = "201", description = "Item added")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDTO createItem(
            @Valid @RequestBody CreateItemDTO createItemDTO,
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        securityService.validateAuthorization(authorization, Feature.ADD_ITEM);
        log.trace("Request to create item");
        return itemService.createItem(createItemDTO);
    }
}
