package com.andres_lozada.demo.controller;

import com.andres_lozada.demo.gateway.dto.CoreQueryResponse;
import com.andres_lozada.demo.gateway.dto.CoreSyncResponse;
import com.andres_lozada.demo.gateway.dto.EventResponse;
import com.andres_lozada.demo.gateway.dto.SyncStatistics;
import com.andres_lozada.demo.gateway.event.CoreEvent;
import com.andres_lozada.demo.service.AuditService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/core-mock")
@Slf4j
@RequiredArgsConstructor
@Hidden
public class CoreMockController {

    private final AuditService auditService;

    @PostMapping("/evento")
    public ResponseEntity<?> receiveEvent(@RequestBody CoreEvent event) {
        if (event == null || event.getEventType() == null || event.getEventType().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "errorCode", "VALIDATION_ERROR",
                    "errorMessage", "eventType is required"));
        }
        log.info("CORE Mock received event: {} - {}", event.getEventType(), event.getRequestId());
        auditService.logCoreEvent(event.getEventType(), event);

        return switch (event.getEventType().toUpperCase()) {
            case "SYNC" -> ResponseEntity.ok(handleSync());
            case "QUERY" -> ResponseEntity.ok(handleQuery(event));
            default -> ResponseEntity.ok(handlePolicyEvent());
        };
    }

    @PostMapping("/query")
    public ResponseEntity<CoreQueryResponse> query(@RequestBody CoreEvent event) {
        if (event == null || event.getQueryType() == null) {
            event = event == null ? new CoreEvent() : event;
            event.setQueryType("POLICIES");
        }
        auditService.logCoreEvent("QUERY", event);
        return ResponseEntity.ok(handleQuery(event));
    }

    private CoreSyncResponse handleSync() {
        return CoreSyncResponse.builder()
                .syncId(UUID.randomUUID())
                .status("SUCCESS")
                .statistics(SyncStatistics.builder()
                        .policiesCreated(15)
                        .policiesUpdated(8)
                        .policiesSkipped(2)
                        .risksCreated(5)
                        .risksUpdated(3)
                        .risksSkipped(0)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private CoreQueryResponse handleQuery(CoreEvent event) {
        Object customerId = event.getCriteria() != null
                ? event.getCriteria().getOrDefault("customerId", event.getCustomerId())
                : event.getCustomerId();
        List<Map<String, Object>> results = List.of(Map.of(
                "externalPolicyId", "CORE-POL-2024-001",
                "customerId", customerId != null ? customerId.toString() : "unknown",
                "policyType", "AUTO",
                "status", "ACTIVE"));
        return CoreQueryResponse.builder()
                .queryId(UUID.randomUUID())
                .type(event.getQueryType() != null ? event.getQueryType() : "POLICIES")
                .results(results)
                .resultCount(results.size())
                .timestamp(LocalDateTime.now())
                .status("SUCCESS")
                .build();
    }

    private EventResponse handlePolicyEvent() {
        return EventResponse.builder()
                .eventId(UUID.randomUUID())
                .status("PROCESSED")
                .message("Policy event processed successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
