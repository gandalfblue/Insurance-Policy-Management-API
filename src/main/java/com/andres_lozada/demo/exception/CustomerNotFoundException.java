package com.andres_lozada.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a Customer is not found.
 */
public class CustomerNotFoundException extends PlatformException {

    public CustomerNotFoundException(String message) {
        super(message, "CUSTOMER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause, "CUSTOMER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
