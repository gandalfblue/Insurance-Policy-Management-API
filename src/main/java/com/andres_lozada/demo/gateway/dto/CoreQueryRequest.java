package com.andres_lozada.demo.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreQueryRequest {

    private String type;
    private Map<String, Object> criteria;
}
