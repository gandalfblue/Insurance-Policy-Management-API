package com.andres_lozada.demo.exception;

import org.springframework.http.HttpStatus;
import java.util.List;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends PlatformException {

    private final List<FieldError> fieldErrors;

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.fieldErrors = List.of();
    }

    public ValidationException(String message, List<FieldError> fieldErrors) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.fieldErrors = fieldErrors;
    }

    public ValidationException(String message, List<FieldError> fieldErrors, Throwable cause) {
        super(message, cause, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public static class FieldError {
        private final String field;
        private final String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}
