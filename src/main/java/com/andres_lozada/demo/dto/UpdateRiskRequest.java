package com.andres_lozada.demo.dto;

import com.andres_lozada.demo.enums.RiskSeverity;
import com.andres_lozada.demo.enums.RiskStatus;
import com.andres_lozada.demo.validator.ValidRiskSeverity;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Request DTO for updating an existing risk.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRiskRequest {

    @Size(max = 255, message = "Risk name must not exceed 255 characters")
    private String riskName;

    @ValidRiskSeverity
    private RiskSeverity riskSeverity;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private RiskStatus status;

    @Size(max = 100, message = "Updated by must not exceed 100 characters")
    private String updatedBy;
}
