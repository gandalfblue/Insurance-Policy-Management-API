package com.andres_lozada.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a Policy is not found.
 */
public class PolicyNotFoundException extends PlatformException {

    public PolicyNotFoundException(String message) {
        super(message, "POLICY_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public PolicyNotFoundException(String message, Throwable cause) {
        super(message, cause, "POLICY_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
