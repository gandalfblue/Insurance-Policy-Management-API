package com.andres_lozada.demo.security;

import com.andres_lozada.demo.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Pattern;

@Component
@Slf4j
public class InputSanitizer {

    private static final Pattern SQL_INJECTION_PATTERN =
            Pattern.compile("(?i)(--|;|\\|\\||\\b(?:SELECT|INSERT|UPDATE|DELETE|DROP|UNION|ALTER|EXEC)\\b|\\b(?:OR|AND)\\s+['\\\"]?\\d+['\\\"]?\\s*=\\s*['\\\"]?\\d+)");

    public String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        String sanitized = input.trim()
                .replaceAll("(?i)<\\s*/?\\s*script\\b[^>]*>", "")
                .replaceAll("[<>\"]", "")
                .replaceAll("(?i)(script|javascript:|on\\w+\\s*=)", "");
        if (SQL_INJECTION_PATTERN.matcher(sanitized).find()) {
            log.warn("Potential SQL injection detected in input");
            throw new ValidationException("Invalid characters in input");
        }
        return sanitized;
    }

    public boolean isValidEmail(String email) {
        return email != null
                && email.matches("^[A-Za-z0-9+_.-]+@(.+)$")
                && email.length() <= 255;
    }

    public boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
