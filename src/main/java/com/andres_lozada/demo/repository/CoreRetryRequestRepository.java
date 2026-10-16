package com.andres_lozada.demo.repository;

import com.andres_lozada.demo.entity.CoreRetryRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CoreRetryRequestRepository extends JpaRepository<CoreRetryRequestEntity, UUID> {

    List<CoreRetryRequestEntity> findByStatusAndNextRetryTimeBefore(String status, LocalDateTime time);
}
