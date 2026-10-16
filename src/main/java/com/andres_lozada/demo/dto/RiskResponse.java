package com.andres_lozada.demo.dto;

import com.andres_lozada.demo.enums.RiskCategory;
import com.andres_lozada.demo.enums.RiskSeverity;
import com.andres_lozada.demo.enums.RiskStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for a risk.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskResponse {

    private UUID riskId;
    private String riskName;
    private RiskCategory riskCategory;
    private RiskSeverity riskSeverity;
    private RiskStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
