package com.andres_lozada.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI insuranceOpenApi() {
        final String apiKeyScheme = "apiKey";
        return new OpenAPI()
                .info(new Info()
                        .title("Insurance Policy Management Platform")
                        .description("REST API for policies, risks, customers and CORE integration")
                        .version("1.0.0")
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development"),
                        new Server().url("https://api.insurance.local").description("Production")))
                .components(new Components().addSecuritySchemes(apiKeyScheme,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("x-api-key")))
                .addSecurityItem(new SecurityRequirement().addList(apiKeyScheme));
    }
}
