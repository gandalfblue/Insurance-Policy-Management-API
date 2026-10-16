package com.andres_lozada.demo.exception;

import com.andres_lozada.demo.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlatformException.class)
    public ResponseEntity<ErrorResponse> handlePlatformException(PlatformException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        if (ex.getHttpStatus().is4xxClientError()) {
            log.warn("{}: {}. TraceId: {}", ex.getErrorCode(), ex.getMessage(), traceId);
        } else {
            log.error("{}: {}. TraceId: {}", ex.getErrorCode(), ex.getMessage(), traceId, ex);
        }

        ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .traceId(traceId);

        if (ex instanceof ValidationException validationEx && !validationEx.getFieldErrors().isEmpty()) {
            List<ErrorResponse.FieldError> fieldErrors = validationEx.getFieldErrors().stream()
                    .map(err -> ErrorResponse.FieldError.builder()
                            .field(err.getField())
                            .message(err.getMessage())
                            .build())
                    .toList();
            builder.fieldErrors(fieldErrors);
        }

        return new ResponseEntity<>(builder.build(), ex.getHttpStatus());
    }

    @ExceptionHandler(CoreGatewayException.class)
    public ResponseEntity<ErrorResponse> handleCoreGatewayException(CoreGatewayException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.error("CORE gateway error. TraceId: {}", traceId, ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("Method argument validation failed. TraceId: {}", traceId);

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> ErrorResponse.FieldError.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build())
                .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .errorMessage("Validation failed for one or more fields")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .traceId(traceId)
                .fieldErrors(fieldErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.error("Unexpected error occurred. TraceId: {}", traceId, ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .errorMessage("An unexpected error occurred. Please contact support.")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
