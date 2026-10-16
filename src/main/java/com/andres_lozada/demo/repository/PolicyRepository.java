package com.andres_lozada.demo.repository;

import com.andres_lozada.demo.entity.PolicyEntity;
import com.andres_lozada.demo.enums.PolicyStatus;
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
 * Spring Data JPA repository for PolicyEntity.
 * Provides CRUD operations and custom query methods for insurance policies.
 */
@Repository
public interface PolicyRepository extends JpaRepository<PolicyEntity, UUID>, JpaSpecificationExecutor<PolicyEntity> {

    @Query("SELECT DISTINCT p FROM PolicyEntity p LEFT JOIN FETCH p.risks WHERE p.policyId = :policyId")
    Optional<PolicyEntity> findByIdWithRisks(@Param("policyId") UUID policyId);

    /**
     * Find policies by customer ID with pagination support.
     *
     * @param customerId the customer's unique identifier
     * @param pageable   pagination information (page number, size, sort)
     * @return paginated list of policies for the specified customer
     */
    @Query("SELECT p FROM PolicyEntity p WHERE p.customerId = :customerId")
    Page<PolicyEntity> findByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);

    /**
     * Find policies by status.
     *
     * @param status the policy status to filter by
     * @return list of policies with the specified status
     */
    @Query("SELECT p FROM PolicyEntity p WHERE p.status = :status")
    List<PolicyEntity> findByStatus(@Param("status") PolicyStatus status);

    /**
     * Find policies by status with pagination support.
     *
     * @param status   the policy status to filter by
     * @param pageable pagination information
     * @return paginated list of policies with the specified status
     */
    @Query("SELECT p FROM PolicyEntity p WHERE p.status = :status")
    Page<PolicyEntity> findByStatus(@Param("status") PolicyStatus status, Pageable pageable);

    /**
     * Find policies by customer ID and status.
     *
     * @param customerId the customer's unique identifier
     * @param status     the policy status to filter by
     * @return list of policies matching both criteria
     */
    @Query("SELECT p FROM PolicyEntity p WHERE p.customerId = :customerId AND p.status = :status")
    List<PolicyEntity> findByCustomerIdAndStatus(@Param("customerId") UUID customerId, @Param("status") PolicyStatus status);

    /**
     * Find policies by customer ID and status with pagination support.
     *
     * @param customerId the customer's unique identifier
     * @param status     the policy status to filter by
     * @param pageable   pagination information
     * @return paginated list of policies matching both criteria
     */
    @Query("SELECT p FROM PolicyEntity p WHERE p.customerId = :customerId AND p.status = :status")
    Page<PolicyEntity> findByCustomerIdAndStatus(@Param("customerId") UUID customerId, @Param("status") PolicyStatus status, Pageable pageable);
}
