package com.andres_lozada.demo.service;

import com.andres_lozada.demo.entity.AuditLogEntity;
import com.andres_lozada.demo.enums.AuditOperation;
import com.andres_lozada.demo.repository.AuditLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logOperation(
            String entityType,
            UUID entityId,
            AuditOperation operation,
            String userId,
            Object oldValues,
            Object newValues,
            String ipAddress) {
        AuditLogEntity entry = AuditLogEntity.builder()
                .entityType(entityType)
                .entityId(entityId != null ? entityId : UUID.randomUUID())
                .operation(operation)
                .userId(userId != null ? userId : currentUserId())
                .oldValues(toJson(oldValues))
                .newValues(toJson(newValues))
                .timestamp(LocalDateTime.now())
                .ipAddress(ipAddress != null ? ipAddress : currentIpAddress())
                .build();
        auditLogRepository.save(entry);
        log.info("Audit {} on {} {} by {}", operation, entityType, entityId, entry.getUserId());
    }

    @Transactional(readOnly = true)
    public List<AuditLogEntity> getAuditHistory(String entityType, UUID entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }

    public void logCoreEvent(String eventType, Object event) {
        logOperation("CORE", UUID.randomUUID(), AuditOperation.CREATE, currentUserId(), null, event, currentIpAddress());
        log.info("CORE event logged: {}", eventType);
    }

    public String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            return auth.getName();
        }
        return "SYSTEM";
    }

    public String currentIpAddress() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String str) {
            return str;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return String.valueOf(value);
        }
    }
}
