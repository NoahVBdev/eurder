package org.switchfully.eurder.eurder.item.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.switchfully.eurder.eurder.item.api.dto.CreateItemDTO;
import org.switchfully.eurder.eurder.item.api.dto.ItemDTO;
import org.switchfully.eurder.eurder.item.service.ItemService;


@RestController
@RequestMapping("/items")
@Tag(name = "Items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(summary = "Add an item", description = "Story 2")
    @ApiResponse(responseCode = "201", description = "Item added")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDTO createItem(@Valid @RequestBody CreateItemDTO createItemDTO) {
        return itemService.createItem(createItemDTO);
    }
}
