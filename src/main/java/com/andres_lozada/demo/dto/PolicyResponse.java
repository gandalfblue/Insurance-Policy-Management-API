package com.andres_lozada.demo.dto;

import com.andres_lozada.demo.enums.PolicyStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a policy with nested risks.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResponse {

    private UUID policyId;
    private UUID customerId;
    private String policyType;
    private PolicyStatus status;
    private BigDecimal coverageAmount;
    private BigDecimal premiumAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private List<RiskResponse> risks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
