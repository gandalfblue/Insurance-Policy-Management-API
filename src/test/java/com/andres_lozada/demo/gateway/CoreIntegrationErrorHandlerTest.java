package com.andres_lozada.demo.gateway;

import com.andres_lozada.demo.config.CoreIntegrationProperties;
import com.andres_lozada.demo.entity.CoreRetryRequestEntity;
import com.andres_lozada.demo.exception.CoreGatewayException;
import com.andres_lozada.demo.repository.CoreRetryRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CoreIntegrationErrorHandlerTest {

    @Test
    void persistsFailedEventForLaterRetry() {
        CoreRetryRequestRepository repository = mock(CoreRetryRequestRepository.class);
        CoreIntegrationProperties properties = new CoreIntegrationProperties();
        var handler = new CoreIntegrationErrorHandler(repository, new ObjectMapper(), mock(RestTemplate.class), properties);
        handler.queueForRetry("SYNC", java.util.Map.of("eventType", "SYNC"), new CoreGatewayException("down"));
        verify(repository).save(argThat(row -> "PENDING".equals(row.getStatus())
                && row.getPayload().contains("SYNC") && row.getNextRetryTime().isAfter(LocalDateTime.now())));
    }

    @Test
    void marksQueuedEventSuccessfulAfterRetry() {
        CoreRetryRequestRepository repository = mock(CoreRetryRequestRepository.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        CoreIntegrationProperties properties = new CoreIntegrationProperties();
        properties.setBaseUrl("http://core.example");
        var row = CoreRetryRequestEntity.builder().eventType("SYNC").payload("{\"eventType\":\"SYNC\"}")
                .retryCount(0).nextRetryTime(LocalDateTime.now().minusMinutes(1)).status("PENDING").build();
        when(repository.findByStatusAndNextRetryTimeBefore(eq("PENDING"), any())).thenReturn(List.of(row));
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(ResponseEntity.ok("ok"));
        var handler = new CoreIntegrationErrorHandler(repository, new ObjectMapper(), restTemplate, properties);

        handler.retryFailedEvents();

        assertEquals("SUCCEEDED", row.getStatus());
        assertEquals(1, row.getRetryCount());
        verify(repository).save(row);
    }

    @Test
    void movesEventToFailedAfterRetryLimit() {
        CoreRetryRequestRepository repository = mock(CoreRetryRequestRepository.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        CoreIntegrationProperties properties = new CoreIntegrationProperties();
        properties.setBaseUrl("http://core.example");
        var row = CoreRetryRequestEntity.builder().retryId(java.util.UUID.randomUUID()).eventType("SYNC")
                .payload("{\"eventType\":\"SYNC\"}").retryCount(2)
                .nextRetryTime(LocalDateTime.now().minusMinutes(1)).status("PENDING").build();
        when(repository.findByStatusAndNextRetryTimeBefore(eq("PENDING"), any())).thenReturn(List.of(row));
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new org.springframework.web.client.ResourceAccessException("offline"));
        var handler = new CoreIntegrationErrorHandler(repository, new ObjectMapper(), restTemplate, properties);

        handler.retryFailedEvents();

        assertEquals("FAILED", row.getStatus());
        assertEquals(3, row.getRetryCount());
        verify(repository).save(row);
    }
}
