package org.switchfully.eurder.eurder.eurder.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.switchfully.eurder.eurder.eurder.api.dto.request.CreateOrderDTO;
import org.switchfully.eurder.eurder.eurder.api.dto.response.OrderDTO;
import org.switchfully.eurder.eurder.eurder.service.OrderService;
import org.switchfully.eurder.eurder.security.domain.AuthenticatedUser;
import org.switchfully.eurder.eurder.security.domain.Feature;
import org.switchfully.eurder.eurder.security.service.SecurityService;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders")
public class OrderController {
    private final OrderService orderService;
    private final SecurityService securityService;

    public OrderController(OrderService orderService, SecurityService securityService) {
        this.orderService = orderService;
        this.securityService = securityService;
    }

    @Operation(summary = "Place an order", description = "Story 3")
    @ApiResponse(responseCode = "201", description = "Order created")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO placeOrder(@Parameter(hidden = true)
                               @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                               @RequestBody CreateOrderDTO createOrderDTO) {
        AuthenticatedUser customer = securityService.validateAuthorization(authorization, Feature.PLACE_ORDER);
        return orderService.placeOrder(customer.email(), createOrderDTO);
    }
}
