package com.andres_lozada.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

@Data
@ConfigurationProperties(prefix = "api.security")
public class ApiKeyProperties {

    private Set<String> validKeys = new HashSet<>();
}
