package com.andres_lozada.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

/**
 * Validator for @PositiveAmount annotation.
 * Validates that a financial amount is positive (greater than zero).
 */
public class PositiveAmountValidator implements ConstraintValidator<PositiveAmount, Number> {

    private boolean allowZero;

    @Override
    public void initialize(PositiveAmount constraintAnnotation) {
        this.allowZero = constraintAnnotation.allowZero();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null values
        }

        BigDecimal amount;
        if (value instanceof BigDecimal) {
            amount = (BigDecimal) value;
        } else if (value instanceof Double || value instanceof Float) {
            amount = BigDecimal.valueOf(value.doubleValue());
        } else {
            amount = new BigDecimal(value.toString());
        }

        if (allowZero) {
            return amount.compareTo(BigDecimal.ZERO) >= 0;
        } else {
            return amount.compareTo(BigDecimal.ZERO) > 0;
        }
    }
}
