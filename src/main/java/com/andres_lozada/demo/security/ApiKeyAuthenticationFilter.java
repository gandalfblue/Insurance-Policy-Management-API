package com.andres_lozada.demo.security;

import com.andres_lozada.demo.config.ApiKeyProperties;
import com.andres_lozada.demo.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "x-api-key";

    private final ApiKeyProperties apiKeyProperties;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("swagger-ui")
                || path.contains("api-docs")
                || path.contains("actuator/health")
                || path.contains("/h2-console")
                || path.startsWith("/core-mock");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Missing API key from: {}", request.getRemoteAddr());
            sendUnauthorizedError(response, "Missing x-api-key header");
            return;
        }
        if (!apiKeyProperties.getValidKeys().contains(apiKey)) {
            log.warn("Invalid API key attempt from: {}", request.getRemoteAddr());
            sendUnauthorizedError(response, "Invalid API key");
            return;
        }
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        "api_consumer",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_API_USER")));
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("UNAUTHORIZED")
                .errorMessage(message)
                .timestamp(LocalDateTime.now())
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
