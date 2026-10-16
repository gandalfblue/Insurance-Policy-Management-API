package com.andres_lozada.demo.security;

import com.andres_lozada.demo.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputSanitizerTest {
    private final InputSanitizer sanitizer = new InputSanitizer();

    @Test
    void trimsAndRemovesMarkupButPreservesNormalNames() {
        assertEquals("O'Neil", sanitizer.sanitizeString("  O'Neil  "));
        assertEquals("alert(1)", sanitizer.sanitizeString("<script>alert(1)</script>"));
    }

    @Test
    void rejectsSqlOperatorsAndKeywords() {
        assertThrows(ValidationException.class, () -> sanitizer.sanitizeString("1 OR 1=1"));
        assertThrows(ValidationException.class, () -> sanitizer.sanitizeString("DROP TABLE customers"));
    }

    @Test
    void validatesEmailAndUuid() {
        assertTrue(sanitizer.isValidEmail("a@example.com"));
        assertFalse(sanitizer.isValidEmail("not-an-email"));
        assertTrue(sanitizer.isValidUUID("00000000-0000-0000-0000-000000000001"));
        assertFalse(sanitizer.isValidUUID("invalid"));
    }
}
