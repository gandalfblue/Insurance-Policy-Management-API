package com.andres_lozada.demo.dto;

import com.andres_lozada.demo.enums.RiskCategory;
import com.andres_lozada.demo.enums.RiskSeverity;
import com.andres_lozada.demo.enums.RiskStatus;
import com.andres_lozada.demo.validator.ValidRiskSeverity;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Request DTO for creating a new risk.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRiskRequest {

    @NotBlank(message = "Risk name is required")
    @Size(max = 255, message = "Risk name must not exceed 255 characters")
    private String riskName;

    @NotNull(message = "Risk category is required")
    private RiskCategory riskCategory;

    @NotNull(message = "Risk severity is required")
    @ValidRiskSeverity
    private RiskSeverity riskSeverity;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private RiskStatus status;
}
