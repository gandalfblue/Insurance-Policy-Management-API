package com.andres_lozada.demo.service;

import com.andres_lozada.demo.audit.Auditable;
import com.andres_lozada.demo.dto.CreateRiskRequest;
import com.andres_lozada.demo.dto.PaginatedResponse;
import com.andres_lozada.demo.dto.RiskResponse;
import com.andres_lozada.demo.dto.UpdateRiskRequest;
import com.andres_lozada.demo.entity.RiskEntity;
import com.andres_lozada.demo.enums.AuditOperation;
import com.andres_lozada.demo.enums.RiskCategory;
import com.andres_lozada.demo.enums.RiskSeverity;
import com.andres_lozada.demo.enums.RiskStatus;
import com.andres_lozada.demo.exception.RiskNotFoundException;
import com.andres_lozada.demo.exception.ValidationException;
import com.andres_lozada.demo.mapper.PaginatedMapper;
import com.andres_lozada.demo.mapper.RiskMapper;
import com.andres_lozada.demo.repository.RiskRepository;
import com.andres_lozada.demo.repository.specification.RiskSpecifications;
import com.andres_lozada.demo.security.InputSanitizer;
import com.andres_lozada.demo.util.PaginationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
@Transactional
public class RiskService {

    private final RiskRepository riskRepository;
    private final RiskMapper riskMapper;
    private final AuditService auditService;
    private final PaginatedMapper paginatedMapper;
    private final InputSanitizer inputSanitizer;

    @Auditable(entity = "RISK", operation = AuditOperation.CREATE)
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 20)
    public RiskResponse createRisk(CreateRiskRequest request) {
        request.setRiskName(inputSanitizer.sanitizeString(request.getRiskName()));
        request.setDescription(inputSanitizer.sanitizeString(request.getDescription()));
        log.info("Creating new risk: {}", request.getRiskName());
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ValidationException("Description is required",
                    List.of(new ValidationException.FieldError("description", "Description must not be empty")));
        }
        if (request.getRiskSeverity() == null) {
            throw new ValidationException("Risk severity is required",
                    List.of(new ValidationException.FieldError("riskSeverity", "Must be HIGH, MEDIUM or LOW")));
        }
        if (riskRepository.findByRiskName(request.getRiskName()).isPresent()) {
            throw new ValidationException("Risk name already exists: " + request.getRiskName(),
                    List.of(new ValidationException.FieldError("riskName", "Risk name must be unique")));
        }

        RiskEntity risk = riskMapper.toEntity(request);
        risk.setStatus(RiskStatus.ACTIVE);
        try {
            RiskEntity savedRisk = riskRepository.save(risk);
            return riskMapper.toResponse(savedRisk);
        } catch (DataIntegrityViolationException ex) {
            throw new ValidationException("Risk name already exists: " + request.getRiskName(),
                    List.of(new ValidationException.FieldError("riskName", "Risk name must be unique")), ex);
        }
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public RiskResponse getRisk(UUID riskId) {
        return riskMapper.toResponse(findOrThrow(riskId));
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public PaginatedResponse<RiskResponse> listRisks(
            String riskCategory,
            String riskSeverity,
            String status,
            Integer pageNumber,
            Integer pageSize) {
        Page<RiskEntity> riskPage = riskRepository.findAll(
                RiskSpecifications.withFilters(
                        parseEnum(RiskCategory.class, riskCategory, "riskCategory"),
                        parseEnum(RiskSeverity.class, riskSeverity, "riskSeverity"),
                        parseEnum(RiskStatus.class, status, "status")),
                PaginationHelper.toPageable(pageNumber, pageSize));
        return paginatedMapper.toPaginatedResponse(riskPage, riskMapper::toResponse);
    }

    @Auditable(entity = "RISK", operation = AuditOperation.UPDATE)
    @Transactional(isolation = Isolation.REPEATABLE_READ, timeout = 15)
    public RiskResponse updateRisk(UUID riskId, UpdateRiskRequest request) {
        request.setRiskName(inputSanitizer.sanitizeString(request.getRiskName()));
        request.setDescription(inputSanitizer.sanitizeString(request.getDescription()));
        RiskEntity existingRisk = findOrThrow(riskId);

        if (request.getRiskName() != null && !request.getRiskName().equals(existingRisk.getRiskName())) {
            if (riskRepository.findByRiskName(request.getRiskName()).isPresent()) {
                throw new ValidationException("Risk name already exists: " + request.getRiskName());
            }
            existingRisk.setRiskName(request.getRiskName());
        }
        if (request.getRiskSeverity() != null) {
            existingRisk.setRiskSeverity(request.getRiskSeverity());
        }
        if (request.getDescription() != null) {
            if (request.getDescription().isBlank()) {
                throw new ValidationException("Description must not be empty");
            }
            existingRisk.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            existingRisk.setStatus(request.getStatus());
        }

        RiskEntity updatedRisk = riskRepository.save(existingRisk);
        return riskMapper.toResponse(updatedRisk);
    }

    @Auditable(entity = "RISK", operation = AuditOperation.DELETE)
    @Transactional(isolation = Isolation.REPEATABLE_READ, timeout = 15)
    public void deleteRisk(UUID riskId) {
        RiskEntity existingRisk = findOrThrow(riskId);
        riskRepository.delete(existingRisk);
    }

    private RiskEntity findOrThrow(UUID riskId) {
        return riskRepository.findById(riskId)
                .orElseThrow(() -> new RiskNotFoundException("Risk not found: " + riskId));
    }

    private <E extends Enum<E>> E parseEnum(Class<E> type, String value, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(type, value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid value for " + field + ": " + value,
                    List.of(new ValidationException.FieldError(field, "Invalid " + field)));
        }
    }
}
