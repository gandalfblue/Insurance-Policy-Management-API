package com.andres_lozada.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception class for the platform.
 * All custom exceptions should extend this class.
 */
public abstract class PlatformException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    protected PlatformException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    protected PlatformException(String message, Throwable cause, String errorCode, HttpStatus httpStatus) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
