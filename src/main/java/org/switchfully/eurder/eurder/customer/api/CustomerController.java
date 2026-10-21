package org.switchfully.eurder.eurder.customer.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.switchfully.eurder.eurder.customer.api.dto.CreateCustomerDTO;
import org.switchfully.eurder.eurder.customer.api.dto.CustomerDTO;
import org.switchfully.eurder.eurder.customer.service.CustomerService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customers")
public class CustomerController {
    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Create a customer account", description = "Story 1")
    @ApiResponse(responseCode = "201", description = "Customer created")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Email already in use", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO createCustomer(@Valid @RequestBody CreateCustomerDTO createCustomerDTO) {
        log.trace("Request to create a new customer");
        return customerService.createCustomer(createCustomerDTO);
    }

    @Operation(summary = "View all customers", description = "Story 7")
    @ApiResponse(responseCode = "200", description = "Get all customers", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CustomerDTO.class))))
    @ApiResponse(responseCode = "404", description = "No customers found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerDTO> getAllCustomers() {
        log.trace("Request to get all customers");
        return customerService.getAllCustomers();
    }

    @Operation(summary = "View a single customer", description = "Story 8")
    @ApiResponse(responseCode = "200", description = "Get a single customers", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class)))
    @ApiResponse(responseCode = "404", description = "No customer found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(path = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDTO getCustomer(@PathVariable UUID id) {
        log.trace("Request to get customer with id " + id.toString());
        return customerService.findCustomerById(id);
    }
}
