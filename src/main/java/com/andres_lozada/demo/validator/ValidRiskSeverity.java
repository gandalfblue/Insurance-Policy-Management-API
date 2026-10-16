package com.andres_lozada.demo.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for risk severity levels.
 * Validates that the risk severity is one of the allowed levels: HIGH, MEDIUM, LOW.
 */
@Documented
@Constraint(validatedBy = RiskSeverityValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRiskSeverity {

    String message() default "Invalid risk severity. Must be HIGH, MEDIUM, or LOW";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
