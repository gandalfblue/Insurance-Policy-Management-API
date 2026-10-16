package com.andres_lozada.demo.dto;

import com.andres_lozada.demo.enums.PolicyStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for updating an existing policy.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePolicyRequest {

    private PolicyStatus status;

    @Positive(message = "Coverage amount must be positive")
    private BigDecimal coverageAmount;

    @Positive(message = "Premium amount must be positive")
    private BigDecimal premiumAmount;

    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDate endDate;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private List<UUID> riskIds;
}
