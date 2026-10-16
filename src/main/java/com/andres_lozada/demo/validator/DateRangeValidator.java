package com.andres_lozada.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.LocalDate;

/**
 * Validator for @DateRange annotation.
 * Validates that the end date field is greater than or equal to the start date field.
 */
public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        this.startField = constraintAnnotation.startField();
        this.endField = constraintAnnotation.endField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null values
        }

        try {
            Field startFieldObj = value.getClass().getDeclaredField(this.startField);
            Field endFieldObj = value.getClass().getDeclaredField(this.endField);
            
            startFieldObj.setAccessible(true);
            endFieldObj.setAccessible(true);
            
            LocalDate startDate = (LocalDate) startFieldObj.get(value);
            LocalDate endDate = (LocalDate) endFieldObj.get(value);
            
            // If both are null, it's valid (let other validators handle null)
            if (startDate == null && endDate == null) {
                return true;
            }
            
            // If start is null but end is not, validate end
            if (startDate == null) {
                return true;
            }
            
            // If end is null, it's valid (optional field)
            if (endDate == null) {
                return true;
            }
            
            // Validate that endDate >= startDate
            return !endDate.isBefore(startDate);
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid field references: " + e.getMessage())
                   .addConstraintViolation();
            return false;
        }
    }
}
