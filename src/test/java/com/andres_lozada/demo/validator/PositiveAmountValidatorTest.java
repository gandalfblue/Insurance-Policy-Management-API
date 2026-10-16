package com.andres_lozada.demo.validator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for PositiveAmountValidator.
 */
class PositiveAmountValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    /**
     * Test class for @PositiveAmount annotation.
     */
    static class AmountContainer {
        @PositiveAmount
        private BigDecimal amount;

        public AmountContainer(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    /**
     * Test class for @PositiveAmount with allowZero = true.
     */
    static class AmountContainerAllowZero {
        @PositiveAmount(allowZero = true)
        private BigDecimal amount;

        public AmountContainerAllowZero(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    @Test
    void testValidPositiveAmount() {
        AmountContainer container = new AmountContainer(new BigDecimal("100.50"));
        Set<ConstraintViolation<AmountContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidPositiveAmount_large() {
        AmountContainer container = new AmountContainer(new BigDecimal("9999999.99"));
        Set<ConstraintViolation<AmountContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidZeroAmount() {
        AmountContainer container = new AmountContainer(BigDecimal.ZERO);
        Set<ConstraintViolation<AmountContainer>> violations = validator.validate(container);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("Amount must be positive")
        ));
    }

    @Test
    void testInvalidNegativeAmount() {
        AmountContainer container = new AmountContainer(new BigDecimal("-50.00"));
        Set<ConstraintViolation<AmountContainer>> violations = validator.validate(container);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("Amount must be positive")
        ));
    }

    @Test
    void testValidZeroAmountWithAllowZero() {
        AmountContainerAllowZero container = new AmountContainerAllowZero(BigDecimal.ZERO);
        Set<ConstraintViolation<AmountContainerAllowZero>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullAmount() {
        AmountContainer container = new AmountContainer(null);
        Set<ConstraintViolation<AmountContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidPositiveDouble() {
        class DoubleContainer {
            @PositiveAmount
            private double amount;

            public DoubleContainer(double amount) {
                this.amount = amount;
            }

            public double getAmount() {
                return amount;
            }
        }
        DoubleContainer container = new DoubleContainer(150.75);
        Set<ConstraintViolation<DoubleContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }
}
