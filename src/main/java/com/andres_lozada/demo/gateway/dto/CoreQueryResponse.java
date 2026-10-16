package com.andres_lozada.demo.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreQueryResponse {

    private UUID queryId;
    private String type;
    private List<Map<String, Object>> results;
    private int resultCount;
    private String status;
    private LocalDateTime timestamp;
}
