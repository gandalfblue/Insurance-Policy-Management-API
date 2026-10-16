package com.andres_lozada.demo.gateway;

import com.andres_lozada.demo.entity.CoreRetryRequestEntity;
import com.andres_lozada.demo.exception.CoreGatewayException;
import com.andres_lozada.demo.repository.CoreRetryRequestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.andres_lozada.demo.config.CoreIntegrationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CoreIntegrationErrorHandler {

    private final CoreRetryRequestRepository retryRequestRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final CoreIntegrationProperties properties;

    public void queueForRetry(String eventType, Object event, CoreGatewayException exception) {
        try {
            CoreRetryRequestEntity retryRequest = CoreRetryRequestEntity.builder()
                    .eventType(eventType)
                    .payload(objectMapper.writeValueAsString(event))
                    .errorMessage(truncate(exception.getMessage()))
                    .retryCount(0)
                    .nextRetryTime(LocalDateTime.now().plusMinutes(5))
                    .status("PENDING")
                    .build();
            retryRequestRepository.save(retryRequest);
            log.warn("Queued CORE event for retry: {}", eventType);
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize CORE retry payload", e);
        }
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void retryFailedEvents() {
        List<CoreRetryRequestEntity> pending = retryRequestRepository
                .findByStatusAndNextRetryTimeBefore("PENDING", LocalDateTime.now());
        for (CoreRetryRequestEntity request : pending) {
            int nextCount = request.getRetryCount() + 1;
            log.info("Retrying CORE event {} attempt {}", request.getEventType(), nextCount);
            try {
                String endpoint = "QUERY".equalsIgnoreCase(request.getEventType())
                        ? "/core-mock/query" : "/core-mock/evento";
                Object payload = objectMapper.readTree(request.getPayload());
                restTemplate.postForEntity(properties.getBaseUrl() + endpoint, payload, String.class);
                request.setStatus("SUCCEEDED");
                request.setRetryCount(nextCount);
                log.info("CORE retry succeeded: {}", request.getRetryId());
            } catch (JsonProcessingException | RestClientException ex) {
                request.setRetryCount(nextCount);
                if (nextCount >= properties.getRetryMaxAttempts()) {
                    request.setStatus("FAILED");
                    log.error("CORE event moved to FAILED after {} attempts: {}", nextCount, request.getRetryId());
                } else {
                    long delayMinutes = (long) Math.ceil(Math.pow(properties.getRetryBackoffMultiplier(), nextCount));
                    request.setNextRetryTime(LocalDateTime.now().plusMinutes(delayMinutes));
                    request.setErrorMessage(truncate(ex.getMessage()));
                }
            }
            retryRequestRepository.save(request);
        }
    }

    private String truncate(String message) {
        if (message == null) {
            return null;
        }
        return message.length() > 1000 ? message.substring(0, 1000) : message;
    }
}
