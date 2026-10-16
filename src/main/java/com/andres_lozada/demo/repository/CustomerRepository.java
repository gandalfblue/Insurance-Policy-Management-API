package com.andres_lozada.demo.repository;

import com.andres_lozada.demo.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for CustomerEntity.
 * Provides CRUD operations and custom query methods for customers.
 * 
 * The email field has a unique constraint at both the database and application level.
 * Use {@link #findByEmail(String)} to check for email uniqueness before creating customers.
 */
@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {

    /**
     * Find customer by email address.
     * The email field has a unique constraint in the database.
     * 
     * @param email the email address to search for
     * @return Optional containing the customer if found, empty otherwise
     */
    @Query("SELECT c FROM CustomerEntity c WHERE c.email = :email")
    Optional<CustomerEntity> findByEmail(@Param("email") String email);
}
