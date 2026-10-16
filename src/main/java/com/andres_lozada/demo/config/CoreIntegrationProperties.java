package com.andres_lozada.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "core.integration")
public class CoreIntegrationProperties {

    private String baseUrl = "http://localhost:8080";
    private int timeout = 30;
    private int retryMaxAttempts = 3;
    private double retryBackoffMultiplier = 2.0;
}
