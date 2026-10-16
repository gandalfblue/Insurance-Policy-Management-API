package com.andres_lozada.demo.validator;

import com.andres_lozada.demo.enums.RiskSeverity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

/**
 * Validator for @ValidRiskSeverity annotation.
 * Validates that the risk severity is one of the allowed levels.
 */
public class RiskSeverityValidator implements ConstraintValidator<ValidRiskSeverity, RiskSeverity> {

    private static final Set<RiskSeverity> VALID_SEVERITIES = Set.of(
        RiskSeverity.HIGH, RiskSeverity.MEDIUM, RiskSeverity.LOW
    );

    @Override
    public boolean isValid(RiskSeverity value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null values
        }
        return VALID_SEVERITIES.contains(value);
    }
}
