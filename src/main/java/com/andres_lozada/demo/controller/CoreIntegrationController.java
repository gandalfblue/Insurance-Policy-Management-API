package com.andres_lozada.demo.controller;

import com.andres_lozada.demo.enums.SyncType;
import com.andres_lozada.demo.gateway.CorePolicyGateway;
import com.andres_lozada.demo.gateway.dto.CoreQueryRequest;
import com.andres_lozada.demo.gateway.dto.CoreQueryResponse;
import com.andres_lozada.demo.gateway.dto.SyncRequest;
import com.andres_lozada.demo.gateway.dto.SyncResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/core")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "CORE Integration")
public class CoreIntegrationController {

    private final CorePolicyGateway corePolicyGateway;

    @PostMapping("/sync")
    @Operation(summary = "Synchronize policies and risks with CORE")
    public ResponseEntity<SyncResponse> sync(@Valid @RequestBody(required = false) SyncRequest request) {
        SyncRequest syncRequest = request != null ? request : SyncRequest.builder().syncType(SyncType.ALL).build();
        if (syncRequest.getSyncType() == null) {
            syncRequest.setSyncType(SyncType.ALL);
        }
        log.info("CORE sync requested: {}", syncRequest.getSyncType());
        return ResponseEntity.ok(corePolicyGateway.syncWithCore(syncRequest));
    }

    @GetMapping("/query")
    @Operation(summary = "Query CORE mock system")
    public ResponseEntity<CoreQueryResponse> query(
            @RequestParam String type,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) String policyStatus) {
        Map<String, Object> criteria = new HashMap<>();
        if (customerId != null) {
            criteria.put("customerId", customerId.toString());
        }
        if (policyStatus != null) {
            criteria.put("policyStatus", policyStatus);
        }
        CoreQueryRequest queryRequest = CoreQueryRequest.builder()
                .type(type)
                .criteria(criteria)
                .build();
        return ResponseEntity.ok(corePolicyGateway.queryCore(queryRequest));
    }
}
