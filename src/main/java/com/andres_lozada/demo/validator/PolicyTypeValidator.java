package com.andres_lozada.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

/**
 * Validator for @ValidPolicyType annotation.
 * Validates that the policy type is one of the allowed types.
 */
public class PolicyTypeValidator implements ConstraintValidator<ValidPolicyType, String> {

    private static final Set<String> VALID_POLICY_TYPES = Set.of(
        "AUTO", "HOME", "HEALTH", "LIFE"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null values
        }
        return VALID_POLICY_TYPES.contains(value.toUpperCase());
    }
}
