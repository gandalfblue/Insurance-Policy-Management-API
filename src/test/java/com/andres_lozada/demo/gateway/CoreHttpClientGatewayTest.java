package com.andres_lozada.demo.gateway;

import com.andres_lozada.demo.config.CoreIntegrationProperties;
import com.andres_lozada.demo.exception.CoreGatewayException;
import com.andres_lozada.demo.gateway.dto.*;
import com.andres_lozada.demo.gateway.event.PolicyEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoreHttpClientGatewayTest {
    @Mock RestTemplate restTemplate;
    @Mock CoreIntegrationErrorHandler errorHandler;
    private CoreIntegrationProperties properties;
    private CoreHttpClientGateway gateway;

    @BeforeEach
    void setUp() {
        properties = new CoreIntegrationProperties();
        properties.setBaseUrl("http://core.example");
        gateway = new CoreHttpClientGateway(restTemplate, properties, errorHandler);
    }

    @Test
    void mapsSyncResponse() {
        var id = UUID.randomUUID();
        var coreResponse = CoreSyncResponse.builder().syncId(id).status("SUCCESS")
                .statistics(SyncStatistics.builder().policiesCreated(2).build())
                .timestamp(LocalDateTime.now()).build();
        when(restTemplate.postForEntity(eq("http://core.example/core-mock/evento"), any(HttpEntity.class),
                eq(CoreSyncResponse.class))).thenReturn(ResponseEntity.ok(coreResponse));

        SyncResponse response = gateway.syncWithCore(SyncRequest.builder().syncType(com.andres_lozada.demo.enums.SyncType.ALL)
                .timeoutSeconds(5).build());
        assertEquals(id, response.getSyncId());
        assertEquals(2, response.getStatistics().getPoliciesCreated());
    }

    @Test
    void mapsQueryResponse() {
        CoreQueryResponse expected = CoreQueryResponse.builder().queryId(UUID.randomUUID()).type("POLICIES")
                .results(java.util.List.of(Map.of("id", "P1"))).resultCount(1).status("SUCCESS").build();
        when(restTemplate.postForEntity(eq("http://core.example/core-mock/query"), any(HttpEntity.class),
                eq(CoreQueryResponse.class))).thenReturn(ResponseEntity.ok(expected));
        var result = gateway.queryCore(CoreQueryRequest.builder().type("POLICIES").criteria(Map.of()).build());
        assertEquals(expected, result);
    }

    @Test
    void sendsPolicyEvent() {
        EventResponse expected = EventResponse.builder().eventId(UUID.randomUUID()).status("PROCESSED").build();
        when(restTemplate.postForEntity(eq("http://core.example/core-mock/evento"), any(HttpEntity.class),
                eq(EventResponse.class))).thenReturn(ResponseEntity.ok(expected));
        assertEquals(expected, gateway.sendPolicyEvent(PolicyEvent.builder().eventType("POLICY_CREATED").build()));
    }

    @Test
    void queuesSyncAfterTransientHttpFailure() {
        SyncRequest request = SyncRequest.builder().syncType(com.andres_lozada.demo.enums.SyncType.ALL).build();
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(CoreSyncResponse.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(CoreGatewayException.class, () -> gateway.syncWithCore(request));
        verify(errorHandler).queueForRetry(eq("SYNC"), any(), any(CoreGatewayException.class));
    }
}
