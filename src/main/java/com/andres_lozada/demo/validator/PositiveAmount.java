package com.andres_lozada.demo.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for positive financial amounts.
 * Validates that a BigDecimal or double value is positive (greater than zero).
 */
@Documented
@Constraint(validatedBy = PositiveAmountValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveAmount {

    String message() default "Amount must be positive (greater than zero)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Whether zero is considered a valid value.
     */
    boolean allowZero() default false;
}
