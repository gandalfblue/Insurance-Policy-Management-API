package com.andres_lozada.demo.repository;

import com.andres_lozada.demo.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for AuditLogEntity.
 * Provides CRUD operations and custom query methods for audit logs.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {

    /**
     * Find audit logs by entity type and entity ID.
     * Useful for retrieving all audit trail for a specific entity.
     *
     * @param entityType the type of entity (e.g., "POLICY", "RISK", "CUSTOMER")
     * @param entityId   the unique identifier of the entity
     * @return list of audit logs for the specified entity
     */
    @Query("SELECT a FROM AuditLogEntity a WHERE a.entityType = :entityType AND a.entityId = :entityId")
    List<AuditLogEntity> findByEntityTypeAndEntityId(@Param("entityType") String entityType, @Param("entityId") UUID entityId);

    /**
     * Find audit logs after a specific timestamp.
     * Useful for retrieving recent audit activity.
     *
     * @param timestamp the starting timestamp (exclusive)
     * @return list of audit logs created after the specified timestamp
     */
    @Query("SELECT a FROM AuditLogEntity a WHERE a.timestamp > :timestamp")
    List<AuditLogEntity> findByTimestampAfter(@Param("timestamp") LocalDateTime timestamp);

    /**
     * Find audit logs by entity type after a specific timestamp.
     * Combines filtering by entity type and time range.
     *
     * @param entityType the type of entity to filter by
     * @param timestamp  the starting timestamp (exclusive)
     * @return list of audit logs matching both criteria
     */
    @Query("SELECT a FROM AuditLogEntity a WHERE a.entityType = :entityType AND a.timestamp > :timestamp")
    List<AuditLogEntity> findByEntityTypeAndTimestampAfter(@Param("entityType") String entityType, @Param("timestamp") LocalDateTime timestamp);

    @Query("SELECT a FROM AuditLogEntity a WHERE a.userId = :userId ORDER BY a.timestamp DESC")
    List<AuditLogEntity> findByUserId(@Param("userId") String userId);

    @Query("SELECT a FROM AuditLogEntity a WHERE a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.timestamp DESC")
    List<AuditLogEntity> findByEntityTypeAndEntityIdOrderByTimestampDesc(
            @Param("entityType") String entityType,
            @Param("entityId") UUID entityId);
}
