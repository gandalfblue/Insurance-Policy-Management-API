package com.andres_lozada.demo.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for date ranges.
 * Validates that a date field meets specific range criteria.
 * 
 * For comparing two fields (e.g., startDate and endDate), use {@link DateRange.List}:
 * 
 * &#64;DateRange.List({
 *     &#64;DateRange(startField = "startDate", endField = "endDate", message = "End date must be after start date")
 * })
 */
@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRange {

    String message() default "Invalid date range";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Name of the start date field.
     */
    String startField();

    /**
     * Name of the end date field.
     */
    String endField();

    /**
     * Defines several &#64;DateRange annotations on the same element.
     */
    @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        DateRange[] value();
    }
}
