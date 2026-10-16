package com.andres_lozada.demo.controller;

import com.andres_lozada.demo.dto.CreateRiskRequest;
import com.andres_lozada.demo.dto.PaginatedResponse;
import com.andres_lozada.demo.dto.RiskResponse;
import com.andres_lozada.demo.dto.UpdateRiskRequest;
import com.andres_lozada.demo.service.RiskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * REST Controller for Risk management operations.
 * Handles CRUD operations for insurance risks.
 */
@RestController
@RequestMapping("/api/risks")
@Slf4j
public class RiskController {

    @Autowired
    private RiskService riskService;

    /**
     * Creates a new insurance risk.
     *
     * @param request the risk creation request
     * @return ResponseEntity with RiskResponse and HTTP 201 Created
     */
    @PostMapping
    public ResponseEntity<RiskResponse> createRisk(@Valid @RequestBody CreateRiskRequest request) {
        log.info("Received request to create risk: {}", request);

        try {
            RiskResponse response = riskService.createRisk(request);
            URI location = URI.create("/api/risks/" + response.getRiskId());

            log.info("Risk created successfully with ID: {}", response.getRiskId());
            return ResponseEntity.created(location).body(response);
        } catch (Exception e) {
            log.error("Error creating risk", e);
            throw e;
        }
    }

    /**
     * Retrieves a specific risk by its ID.
     *
     * @param riskId the UUID of the risk to retrieve
     * @return ResponseEntity with RiskResponse and HTTP 200 OK
     */
    @GetMapping("/{riskId}")
    public ResponseEntity<RiskResponse> getRisk(@PathVariable UUID riskId) {
        log.info("Received request to get risk with ID: {}", riskId);

        try {
            RiskResponse response = riskService.getRisk(riskId);

            log.info("Risk retrieved successfully with ID: {}", riskId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving risk with ID: {}", riskId, e);
            throw e;
        }
    }

    /**
     * Retrieves a paginated list of risks with optional filters.
     *
     * @param riskCategory  optional risk category filter
     * @param riskSeverity  optional risk severity filter
     * @param status        optional risk status filter
     * @param pageNumber    page number (0-based, default: 0)
     * @param pageSize      page size (default: 10)
     * @return ResponseEntity with PaginatedResponse<RiskResponse> and HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<RiskResponse>> listRisks(
            @RequestParam(required = false) String riskCategory,
            @RequestParam(required = false) String riskSeverity,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        log.info("Received request to list risks with filters - riskCategory: {}, riskSeverity: {}, status: {}, pageNumber: {}, pageSize: {}",
                riskCategory, riskSeverity, status, pageNumber, pageSize);

        try {
            PaginatedResponse<RiskResponse> response = riskService.listRisks(riskCategory, riskSeverity, status, pageNumber, pageSize);

            log.info("Risks listed successfully. Total count: {}", response.getPagination().getTotalCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error listing risks", e);
            throw e;
        }
    }

    /**
     * Updates an existing risk by its ID.
     *
     * @param riskId  the UUID of the risk to update
     * @param request the update request with new risk data
     * @return ResponseEntity with updated RiskResponse and HTTP 200 OK
     */
    @PutMapping("/{riskId}")
    public ResponseEntity<RiskResponse> updateRisk(
            @PathVariable UUID riskId,
            @Valid @RequestBody UpdateRiskRequest request) {

        log.info("Received request to update risk with ID: {} - {}", riskId, request);

        try {
            RiskResponse response = riskService.updateRisk(riskId, request);

            log.info("Risk updated successfully with ID: {}", riskId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating risk with ID: {}", riskId, e);
            throw e;
        }
    }

    /**
     * Deletes a risk by its ID.
     *
     * @param riskId the UUID of the risk to delete
     * @return ResponseEntity with HTTP 204 No Content
     */
    @DeleteMapping("/{riskId}")
    public ResponseEntity<Void> deleteRisk(@PathVariable UUID riskId) {
        log.info("Received request to delete risk with ID: {}", riskId);

        try {
            riskService.deleteRisk(riskId);

            log.info("Risk deleted successfully with ID: {}", riskId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting risk with ID: {}", riskId, e);
            throw e;
        }
    }
}
