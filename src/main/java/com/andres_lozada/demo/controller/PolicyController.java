package com.andres_lozada.demo.controller;

import com.andres_lozada.demo.dto.CreatePolicyRequest;
import com.andres_lozada.demo.dto.PaginatedResponse;
import com.andres_lozada.demo.dto.PolicyResponse;
import com.andres_lozada.demo.dto.UpdatePolicyRequest;
import com.andres_lozada.demo.service.PolicyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * REST Controller for Policy management operations.
 * Handles CRUD operations for insurance policies.
 */
@RestController
@RequestMapping("/api/policies")
@Slf4j
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    /**
     * Creates a new insurance policy.
     *
     * @param request the policy creation request
     * @return ResponseEntity with PolicyResponse and HTTP 201 Created
     */
    @PostMapping
    public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody CreatePolicyRequest request) {
        log.info("Received request to create policy: {}", request);

        try {
            PolicyResponse response = policyService.createPolicy(request);
            URI location = URI.create("/api/policies/" + response.getPolicyId());

            log.info("Policy created successfully with ID: {}", response.getPolicyId());
            return ResponseEntity.created(location).body(response);
        } catch (Exception e) {
            log.error("Error creating policy", e);
            throw e;
        }
    }

    /**
     * Retrieves a specific policy by its ID.
     *
     * @param policyId the UUID of the policy to retrieve
     * @return ResponseEntity with PolicyResponse and HTTP 200 OK
     */
    @GetMapping("/{policyId}")
    public ResponseEntity<PolicyResponse> getPolicy(@PathVariable UUID policyId) {
        log.info("Received request to get policy with ID: {}", policyId);

        try {
            PolicyResponse response = policyService.getPolicy(policyId);

            log.info("Policy retrieved successfully with ID: {}", policyId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving policy with ID: {}", policyId, e);
            throw e;
        }
    }

    /**
     * Retrieves a paginated list of policies with optional filters.
     *
     * @param customerId  optional customer ID filter
     * @param status      optional policy status filter
     * @param pageNumber  page number (0-based, default: 0)
     * @param pageSize    page size (default: 10)
     * @return ResponseEntity with PaginatedResponse<PolicyResponse> and HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<PolicyResponse>> listPolicies(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        log.info("Received request to list policies with filters - customerId: {}, status: {}, pageNumber: {}, pageSize: {}",
                customerId, status, pageNumber, pageSize);

        try {
            PaginatedResponse<PolicyResponse> response = policyService.listPolicies(customerId, status, pageNumber, pageSize);

            log.info("Policies listed successfully. Total count: {}", response.getPagination().getTotalCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error listing policies", e);
            throw e;
        }
    }

    /**
     * Updates an existing policy by its ID.
     *
     * @param policyId  the UUID of the policy to update
     * @param request   the update request with new policy data
     * @return ResponseEntity with updated PolicyResponse and HTTP 200 OK
     */
    @PutMapping("/{policyId}")
    public ResponseEntity<PolicyResponse> updatePolicy(
            @PathVariable UUID policyId,
            @Valid @RequestBody UpdatePolicyRequest request) {

        log.info("Received request to update policy with ID: {} - {}", policyId, request);

        try {
            PolicyResponse response = policyService.updatePolicy(policyId, request);

            log.info("Policy updated successfully with ID: {}", policyId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating policy with ID: {}", policyId, e);
            throw e;
        }
    }

    /** Deletes a policy by its ID. */
    @DeleteMapping("/{policyId}")
    public ResponseEntity<Void> deletePolicy(@PathVariable UUID policyId) {
        log.info("Received request to delete policy with ID: {}", policyId);
        try {
            policyService.deletePolicy(policyId);
            log.info("Policy deleted successfully with ID: {}", policyId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting policy with ID: {}", policyId, e);
            throw e;
        }
    }
}
