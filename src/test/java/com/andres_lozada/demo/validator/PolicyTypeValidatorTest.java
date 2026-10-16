package com.andres_lozada.demo.validator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for PolicyTypeValidator.
 */
class PolicyTypeValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    /**
     * Test class for @ValidPolicyType annotation.
     */
    static class PolicyTypeContainer {
        @ValidPolicyType
        private String policyType;

        public PolicyTypeContainer(String policyType) {
            this.policyType = policyType;
        }

        public String getPolicyType() {
            return policyType;
        }

        public void setPolicyType(String policyType) {
            this.policyType = policyType;
        }
    }

    @Test
    void testValidPolicyType_AUTO() {
        PolicyTypeContainer container = new PolicyTypeContainer("AUTO");
        Set<ConstraintViolation<PolicyTypeContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidPolicyType_HOME() {
        PolicyTypeContainer container = new PolicyTypeContainer("HOME");
        Set<ConstraintViolation<PolicyTypeContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidPolicyType_HEALTH() {
        PolicyTypeContainer container = new PolicyTypeContainer("HEALTH");
        Set<ConstraintViolation<PolicyTypeContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidPolicyType_LIFE() {
        PolicyTypeContainer container = new PolicyTypeContainer("LIFE");
        Set<ConstraintViolation<PolicyTypeContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidPolicyType_lowercase() {
        PolicyTypeContainer container = new PolicyTypeContainer("auto");
        Set<ConstraintViolation<PolicyTypeContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidPolicyType() {
        PolicyTypeContainer container = new PolicyTypeContainer("INVALID");
        Set<ConstraintViolation<PolicyTypeContainer>> violations = validator.validate(container);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("Invalid policy type")
        ));
    }

    @Test
    void testNullPolicyType() {
        PolicyTypeContainer container = new PolicyTypeContainer(null);
        Set<ConstraintViolation<PolicyTypeContainer>> violations = validator.validate(container);
        assertTrue(violations.isEmpty());
    }
}
