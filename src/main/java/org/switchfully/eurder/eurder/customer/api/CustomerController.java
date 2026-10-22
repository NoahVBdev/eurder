package org.switchfully.eurder.eurder.customer.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.switchfully.eurder.eurder.customer.api.dto.CreateCustomerDTO;
import org.switchfully.eurder.eurder.customer.api.dto.CustomerDTO;
import org.switchfully.eurder.eurder.customer.exception.EmailAlreadyInUseException;
import org.switchfully.eurder.eurder.customer.service.CustomerService;
import org.switchfully.eurder.eurder.security.domain.Feature;
import org.switchfully.eurder.eurder.security.service.SecurityService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customers")
public class CustomerController {
    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;
    private final SecurityService securityService;

    public CustomerController(CustomerService customerService, SecurityService securityService) {
        this.customerService = customerService;
        this.securityService = securityService;
    }

    @Operation(summary = "Create a customer account", description = "Story 1")
    @ApiResponse(responseCode = "201", description = "Customer created")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Email already in use", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO createCustomer(
            @Valid @RequestBody CreateCustomerDTO createCustomerDTO) {
        log.trace("Request to create a new customer");
        return customerService.createCustomer(createCustomerDTO);
    }

    @Operation(summary = "View all customers", description = "Story 7")
    @ApiResponse(responseCode = "200", description = "Get all customers", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CustomerDTO.class))))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "No customers found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerDTO> getAllCustomers(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.trace("Request to get all customers");
        securityService.validateAuthorization(authorization, Feature.VIEW_ALL_CUSTOMERS);
        return customerService.getAllCustomers();
    }

    @Operation(summary = "View a single customer", description = "Story 8")
    @ApiResponse(responseCode = "200", description = "Get a single customers", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "No customer found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(path = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDTO getCustomer(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.trace("Request to get customer with id " + id.toString());
        securityService.validateAuthorization(authorization, Feature.VIEW_CUSTOMER);
        return customerService.findCustomerById(id);
    }
}
