package com.andres_lozada.demo.dto;

import com.andres_lozada.demo.validator.DateRange;
import com.andres_lozada.demo.validator.ValidPolicyType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating a new policy.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DateRange.List({
    @DateRange(startField = "startDate", endField = "endDate", message = "End date must be after or equal to start date")
})
public class CreatePolicyRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotBlank(message = "Policy type is required")
    @Size(max = 50, message = "Policy type must not exceed 50 characters")
    @ValidPolicyType
    private String policyType;

    @NotNull(message = "Coverage amount is required")
    @Positive(message = "Coverage amount must be positive")
    private BigDecimal coverageAmount;

    @NotNull(message = "Premium amount is required")
    @Positive(message = "Premium amount must be positive")
    private BigDecimal premiumAmount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDate endDate;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private List<UUID> riskIds;
}
