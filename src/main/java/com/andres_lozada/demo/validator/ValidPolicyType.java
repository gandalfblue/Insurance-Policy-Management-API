package com.andres_lozada.demo.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for policy types.
 * Validates that the policy type is one of the allowed types: AUTO, HOME, HEALTH, LIFE.
 */
@Documented
@Constraint(validatedBy = PolicyTypeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPolicyType {

    String message() default "Invalid policy type. Must be AUTO, HOME, HEALTH, or LIFE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
