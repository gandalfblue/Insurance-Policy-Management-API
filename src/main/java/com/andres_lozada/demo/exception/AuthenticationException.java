package com.andres_lozada.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails (missing or invalid API key).
 */
public class AuthenticationException extends PlatformException {

    public AuthenticationException(String message) {
        super(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}
