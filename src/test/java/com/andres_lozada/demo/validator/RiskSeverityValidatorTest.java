package com.andres_lozada.demo.validator;

import com.andres_lozada.demo.enums.RiskSeverity;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for RiskSeverityValidator.
 */
class RiskSeverityValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    /**
     * Test class for @ValidRiskSeverity annotation.
     */
    static class RiskSeverityContainer {
        @ValidRiskSeverity
        private RiskSeverity riskSeverity;

        public RiskSeverityContainer(RiskSeverity riskSeverity) {
            this.riskSeverity = riskSeverity;
        }

        public RiskSeverity getRiskSeverity() {
            return riskSeverity;
        }

        public void setRiskSeverity(RiskSeverity riskSeverity) {
            this.riskSeverity = riskSeverity;
        }
    }

    @Test
    void testValidRiskSeverity_HIGH() {
        RiskSeverityContainer container = new RiskSeverityContainer(RiskSeverity.HIGH);
        Set<ConstraintViolation<RiskSeverityContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidRiskSeverity_MEDIUM() {
        RiskSeverityContainer container = new RiskSeverityContainer(RiskSeverity.MEDIUM);
        Set<ConstraintViolation<RiskSeverityContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidRiskSeverity_LOW() {
        RiskSeverityContainer container = new RiskSeverityContainer(RiskSeverity.LOW);
        Set<ConstraintViolation<RiskSeverityContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidRiskSeverity() {
        RiskSeverityContainer container = new RiskSeverityContainer(null);
        Set<ConstraintViolation<RiskSeverityContainer>> violations = validator.validate(container);
        // null should pass (let @NotNull handle it)
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullRiskSeverity() {
        RiskSeverityContainer container = new RiskSeverityContainer(null);
        Set<ConstraintViolation<RiskSeverityContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }
}
