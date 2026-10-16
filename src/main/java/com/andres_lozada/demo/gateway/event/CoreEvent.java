package com.andres_lozada.demo.gateway.event;

import com.andres_lozada.demo.enums.SyncType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoreEvent {

    private String eventType;
    private UUID requestId;
    private LocalDateTime timestamp;
    private SyncType syncType;
    private Integer timeoutSeconds;
    private String queryType;
    private Map<String, Object> criteria;
    private UUID policyId;
    private UUID customerId;
    private BigDecimal coverageAmount;
    private String policyType;
    private String status;
}
