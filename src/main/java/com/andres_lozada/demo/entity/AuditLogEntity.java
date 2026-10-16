package com.andres_lozada.demo.entity;

import com.andres_lozada.demo.enums.AuditOperation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Audit Log Entity for tracking important system operations.
 * Logs create, update, and delete operations on policies, risks, and customers.
 */
@Entity
@Table(name = "audit_log", indexes = {
    @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_user", columnList = "user_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "audit_id")
    private UUID auditId;

    @NotNull(message = "Entity type is required")
    @Size(max = 50, message = "Entity type must not exceed 50 characters")
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @NotNull(message = "Entity ID is required")
    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @NotNull(message = "Operation is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false, length = 20)
    private AuditOperation operation;

    @Size(max = 100, message = "User ID must not exceed 100 characters")
    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues;

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Size(max = 45, message = "IP address must not exceed 45 characters")
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @PrePersist
    public void prePersist() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
