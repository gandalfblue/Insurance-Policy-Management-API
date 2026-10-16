package com.andres_lozada.demo.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String errorCode;
    private String errorMessage;
    private LocalDateTime timestamp;
    private String path;
    private String traceId;
    private List<FieldError> fieldErrors;

    /**
     * Field-level error details.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}
