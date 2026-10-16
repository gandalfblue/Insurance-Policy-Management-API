package com.andres_lozada.demo.aspect;

import com.andres_lozada.demo.audit.Auditable;
import com.andres_lozada.demo.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditingAspect {

    private final AuditService auditService;

    @Around("@annotation(auditable)")
    public Object auditOperation(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long duration = System.currentTimeMillis() - start;

        UUID entityId = extractEntityId(pjp.getArgs(), result);
        auditService.logOperation(
                auditable.entity(),
                entityId,
                auditable.operation(),
                auditService.currentUserId(),
                Arrays.asList(pjp.getArgs()),
                result,
                auditService.currentIpAddress());

        log.info("Auditable {} {} completed in {}ms", auditable.operation(), auditable.entity(), duration);
        return result;
    }

    private UUID extractEntityId(Object[] args, Object result) {
        for (Object arg : args) {
            if (arg instanceof UUID uuid) {
                return uuid;
            }
        }
        if (result != null) {
            try {
                var method = result.getClass().getMethod("getPolicyId");
                Object value = method.invoke(result);
                if (value instanceof UUID uuid) {
                    return uuid;
                }
            } catch (Exception ignored) {
                // try other identifiers below
            }
            try {
                var method = result.getClass().getMethod("getRiskId");
                Object value = method.invoke(result);
                if (value instanceof UUID uuid) {
                    return uuid;
                }
            } catch (Exception ignored) {
                // try customer id
            }
            try {
                var method = result.getClass().getMethod("getCustomerId");
                Object value = method.invoke(result);
                if (value instanceof UUID uuid) {
                    return uuid;
                }
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }
}
