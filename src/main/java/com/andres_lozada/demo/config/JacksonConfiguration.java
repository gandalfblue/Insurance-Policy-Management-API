package com.andres_lozada.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {
    @Bean
    public ObjectMapper applicationObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}
