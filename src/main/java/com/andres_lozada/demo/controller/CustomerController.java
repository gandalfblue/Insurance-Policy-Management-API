package com.andres_lozada.demo.controller;

import com.andres_lozada.demo.dto.CreateCustomerRequest;
import com.andres_lozada.demo.dto.CustomerResponse;
import com.andres_lozada.demo.dto.UpdateCustomerRequest;
import com.andres_lozada.demo.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create customer")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        log.info("Creating customer: {}", request.getEmail());
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.created(URI.create("/api/customers/" + response.getCustomerId())).body(response);
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by id")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerService.getCustomer(customerId));
    }

    @PutMapping("/{customerId}")
    @Operation(summary = "Update customer")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable UUID customerId,
            @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(customerId, request));
    }
}
