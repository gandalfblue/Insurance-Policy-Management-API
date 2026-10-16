package com.andres_lozada.demo.repository;

import com.andres_lozada.demo.entity.RiskEntity;
import com.andres_lozada.demo.enums.RiskCategory;
import com.andres_lozada.demo.enums.RiskSeverity;
import com.andres_lozada.demo.enums.RiskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for RiskEntity.
 * Provides CRUD operations and custom query methods for insurance risks.
 */
@Repository
public interface RiskRepository extends JpaRepository<RiskEntity, UUID>, JpaSpecificationExecutor<RiskEntity> {

    /**
     * Find risk by risk name.
     *
     * @param riskName the risk name to find
     * @return Optional containing the risk if found
     */
    @Query("SELECT r FROM RiskEntity r WHERE r.riskName = :riskName")
    Optional<RiskEntity> findByRiskName(@Param("riskName") String riskName);

    /**
     * Find risks by risk category.
     *
     * @param riskCategory the risk category to filter by
     * @return list of risks in the specified category
     */
    @Query("SELECT r FROM RiskEntity r WHERE r.riskCategory = :riskCategory")
    List<RiskEntity> findByRiskCategory(@Param("riskCategory") RiskCategory riskCategory);

    /**
     * Find risks by risk category with pagination support.
     *
     * @param riskCategory the risk category to filter by
     * @param pageable     pagination information
     * @return paginated list of risks in the specified category
     */
    @Query("SELECT r FROM RiskEntity r WHERE r.riskCategory = :riskCategory")
    Page<RiskEntity> findByRiskCategory(@Param("riskCategory") RiskCategory riskCategory, Pageable pageable);

    /**
     * Find risks by risk severity.
     *
     * @param riskSeverity the risk severity to filter by
     * @return list of risks with the specified severity
     */
    @Query("SELECT r FROM RiskEntity r WHERE r.riskSeverity = :riskSeverity")
    List<RiskEntity> findByRiskSeverity(@Param("riskSeverity") RiskSeverity riskSeverity);

    /**
     * Find risks by risk severity with pagination support.
     *
     * @param riskSeverity the risk severity to filter by
     * @param pageable     pagination information
     * @return paginated list of risks with the specified severity
     */
    @Query("SELECT r FROM RiskEntity r WHERE r.riskSeverity = :riskSeverity")
    Page<RiskEntity> findByRiskSeverity(@Param("riskSeverity") RiskSeverity riskSeverity, Pageable pageable);

    /**
     * Find risks by status.
     *
     * @param status the risk status to filter by
     * @return list of risks with the specified status
     */
    @Query("SELECT r FROM RiskEntity r WHERE r.status = :status")
    List<RiskEntity> findByStatus(@Param("status") RiskStatus status);

    /**
     * Find risks by status with pagination support.
     *
     * @param status   the risk status to filter by
     * @param pageable pagination information
     * @return paginated list of risks with the specified status
     */
    @Query("SELECT r FROM RiskEntity r WHERE r.status = :status")
    Page<RiskEntity> findByStatus(@Param("status") RiskStatus status, Pageable pageable);

    /**
     * Find risks by risk category and risk severity with pagination support.
     *
     * @param riskCategory the risk category to filter by
     * @param riskSeverity the risk severity to filter by
     * @param pageable     pagination information
     * @return paginated list of risks matching both criteria
     */
    @Query("SELECT r FROM RiskEntity r WHERE r.riskCategory = :riskCategory AND r.riskSeverity = :riskSeverity")
    Page<RiskEntity> findByRiskCategoryAndRiskSeverity(
            @Param("riskCategory") RiskCategory riskCategory,
            @Param("riskSeverity") RiskSeverity riskSeverity,
            Pageable pageable);

    /**
     * Find risks by risk category and status with pagination support.
     *
     * @param riskCategory the risk category to filter by
     * @param status       the risk status to filter by
     * @param pageable     pagination information
     * @return paginated list of risks matching both criteria
     */
    @Query("SELECT r FROM RiskEntity r WHERE r.riskCategory = :riskCategory AND r.status = :status")
    Page<RiskEntity> findByRiskCategoryAndStatus(
            @Param("riskCategory") RiskCategory riskCategory,
            @Param("status") RiskStatus status,
            Pageable pageable);

    /**
     * Find risks by risk severity and status with pagination support.
     *
     * @param riskSeverity the risk severity to filter by
     * @param status       the risk status to filter by
     * @param pageable     pagination information
     * @return paginated list of risks matching both criteria
     */
    @Query("SELECT r FROM RiskEntity r WHERE r.riskSeverity = :riskSeverity AND r.status = :status")
    Page<RiskEntity> findByRiskSeverityAndStatus(
            @Param("riskSeverity") RiskSeverity riskSeverity,
            @Param("status") RiskStatus status,
            Pageable pageable);

    /**
     * Find risks by risk category, risk severity, and status with pagination support.
     *
     * @param riskCategory the risk category to filter by
     * @param riskSeverity the risk severity to filter by
     * @param status       the risk status to filter by
     * @param pageable     pagination information
     * @return paginated list of risks matching all criteria
     */
    @Query("SELECT r FROM RiskEntity r WHERE r.riskCategory = :riskCategory AND r.riskSeverity = :riskSeverity AND r.status = :status")
    Page<RiskEntity> findByRiskCategoryAndRiskSeverityAndStatus(
            @Param("riskCategory") RiskCategory riskCategory,
            @Param("riskSeverity") RiskSeverity riskSeverity,
            @Param("status") RiskStatus status,
            Pageable pageable);
}
