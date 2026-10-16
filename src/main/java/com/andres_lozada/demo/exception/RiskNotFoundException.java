package com.andres_lozada.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a Risk is not found.
 */
public class RiskNotFoundException extends PlatformException {

    public RiskNotFoundException(String message) {
        super(message, "RISK_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public RiskNotFoundException(String message, Throwable cause) {
        super(message, cause, "RISK_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
