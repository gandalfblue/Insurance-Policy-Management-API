package com.andres_lozada.demo.validator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for DateRangeValidator.
 */
class DateRangeValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    /**
     * Test class for @DateRange annotation.
     */
    @DateRange.List({
        @DateRange(startField = "startDate", endField = "endDate", message = "End date must be after start date")
    })
    static class DateRangeContainer {
        private LocalDate startDate;
        private LocalDate endDate;

        public DateRangeContainer(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }
    }

    @Test
    void testValidDateRange_startBeforeEnd() {
        DateRangeContainer container = new DateRangeContainer(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 12, 31)
        );
        
        Set<ConstraintViolation<DateRangeContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidDateRange_sameDate() {
        DateRangeContainer container = new DateRangeContainer(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 1)
        );
        
        Set<ConstraintViolation<DateRangeContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidDateRange_endBeforeStart() {
        DateRangeContainer container = new DateRangeContainer(
            LocalDate.of(2024, 12, 31),
            LocalDate.of(2024, 1, 1)
        );
        
        Set<ConstraintViolation<DateRangeContainer>> violations = validator.validate(container);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("End date must be after start date")
        ));
    }

    @Test
    void testValidDateRange_nullEndDate() {
        DateRangeContainer container = new DateRangeContainer(
            LocalDate.of(2024, 1, 1),
            null
        );
        
        Set<ConstraintViolation<DateRangeContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidDateRange_nullStartDate() {
        DateRangeContainer container = new DateRangeContainer(
            null,
            LocalDate.of(2024, 12, 31)
        );
        
        Set<ConstraintViolation<DateRangeContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidDateRange_bothNull() {
        DateRangeContainer container = new DateRangeContainer(null, null);
        
        Set<ConstraintViolation<DateRangeContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }
}
