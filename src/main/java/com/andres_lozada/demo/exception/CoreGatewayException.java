package com.andres_lozada.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when CORE system integration fails.
 */
public class CoreGatewayException extends RuntimeException {

    private final String errorCode = "CORE_GATEWAY_ERROR";
    private final HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

    public CoreGatewayException(String message) {
        super(message);
    }

    public CoreGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
