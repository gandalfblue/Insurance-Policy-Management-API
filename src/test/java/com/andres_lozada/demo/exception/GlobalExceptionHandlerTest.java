package com.andres_lozada.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private HttpServletRequest request() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/policies/123");
        return request;
    }

    @Test
    void mapsPlatformExceptionAndAddsTraceId() {
        var response = handler.handlePlatformException(new PolicyNotFoundException("missing"), request());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("/api/policies/123", response.getBody().getPath());
        assertNotNull(response.getBody().getTraceId());
    }

    @Test
    void mapsValidationFieldDetails() {
        var error = new ValidationException("invalid", List.of(
                new ValidationException.FieldError("coverageAmount", "must be positive")));
        var response = handler.handlePlatformException(error, request());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("coverageAmount", response.getBody().getFieldErrors().getFirst().getField());
    }

    @Test
    void mapsUnexpectedErrorsToSafeResponse() {
        var response = handler.handleGenericException(new IllegalStateException("secret details"), request());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().getErrorMessage().contains("secret details"));
    }
}
