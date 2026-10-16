package com.andres_lozada.demo.gateway;

import com.andres_lozada.demo.config.CoreIntegrationProperties;
import com.andres_lozada.demo.exception.CoreGatewayException;
import com.andres_lozada.demo.gateway.dto.CoreQueryRequest;
import com.andres_lozada.demo.gateway.dto.CoreQueryResponse;
import com.andres_lozada.demo.gateway.dto.CoreSyncResponse;
import com.andres_lozada.demo.gateway.dto.EventResponse;
import com.andres_lozada.demo.gateway.dto.SyncRequest;
import com.andres_lozada.demo.gateway.dto.SyncResponse;
import com.andres_lozada.demo.gateway.event.CoreQueryEvent;
import com.andres_lozada.demo.gateway.event.CoreSyncEvent;
import com.andres_lozada.demo.gateway.event.PolicyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Profile("!test")
@Slf4j
@RequiredArgsConstructor
public class CoreHttpClientGateway implements CorePolicyGateway {

    private final RestTemplate restTemplate;
    private final CoreIntegrationProperties coreProperties;
    private final CoreIntegrationErrorHandler errorHandler;

    @Override
    @Retryable(retryFor = CoreGatewayException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public SyncResponse syncWithCore(SyncRequest syncRequest) throws CoreGatewayException {
        String url = coreProperties.getBaseUrl() + "/core-mock/evento";
        log.info("CORE sync request to {} type={}", url, syncRequest.getSyncType());
        CoreSyncEvent syncEvent = CoreSyncEvent.builder()
                .eventType("SYNC")
                .syncType(syncRequest.getSyncType())
                .timeoutSeconds(syncRequest.getTimeoutSeconds())
                .timestamp(LocalDateTime.now())
                .requestId(UUID.randomUUID())
                .build();
        try {
            HttpHeaders headers = jsonHeaders();
            headers.set("X-Sync-Timeout", String.valueOf(syncRequest.getTimeoutSeconds()));
            ResponseEntity<CoreSyncResponse> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(syncEvent, headers), CoreSyncResponse.class);
            CoreSyncResponse body = response.getBody();
            if (body == null) {
                throw new CoreGatewayException("Empty CORE sync response");
            }
            return SyncResponse.builder()
                    .syncId(body.getSyncId())
                    .status(body.getStatus())
                    .statistics(body.getStatistics())
                    .timestamp(body.getTimestamp())
                    .build();
        } catch (CoreGatewayException ex) {
            errorHandler.queueForRetry("SYNC", syncEvent, ex);
            throw ex;
        } catch (RestClientException ex) {
            CoreGatewayException wrapped = new CoreGatewayException("Failed to sync with CORE system", ex);
            errorHandler.queueForRetry("SYNC", syncEvent, wrapped);
            throw wrapped;
        }
    }

    @Override
    @Retryable(retryFor = CoreGatewayException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public CoreQueryResponse queryCore(CoreQueryRequest queryRequest) throws CoreGatewayException {
        String url = coreProperties.getBaseUrl() + "/core-mock/query";
        log.info("CORE query request type={}", queryRequest.getType());
        CoreQueryEvent queryEvent = CoreQueryEvent.builder()
                .eventType("QUERY")
                .queryType(queryRequest.getType())
                .criteria(queryRequest.getCriteria())
                .timestamp(LocalDateTime.now())
                .requestId(UUID.randomUUID())
                .build();
        try {
            ResponseEntity<CoreQueryResponse> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(queryEvent, jsonHeaders()), CoreQueryResponse.class);
            return response.getBody();
        } catch (CoreGatewayException ex) {
            errorHandler.queueForRetry("QUERY", queryEvent, ex);
            throw ex;
        } catch (RestClientException ex) {
            CoreGatewayException wrapped = new CoreGatewayException("Failed to query CORE system", ex);
            errorHandler.queueForRetry("QUERY", queryEvent, wrapped);
            throw wrapped;
        }
    }

    @Override
    @Retryable(retryFor = CoreGatewayException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public EventResponse sendPolicyEvent(PolicyEvent event) throws CoreGatewayException {
        String url = coreProperties.getBaseUrl() + "/core-mock/evento";
        try {
            ResponseEntity<EventResponse> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(event, jsonHeaders()), EventResponse.class);
            return response.getBody();
        } catch (RestClientException ex) {
            CoreGatewayException wrapped = new CoreGatewayException("Failed to send policy event", ex);
            errorHandler.queueForRetry("EVENT", event, wrapped);
            throw wrapped;
        }
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
