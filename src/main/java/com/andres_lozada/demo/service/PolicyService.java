package com.andres_lozada.demo.service;

import com.andres_lozada.demo.audit.Auditable;
import com.andres_lozada.demo.dto.CreatePolicyRequest;
import com.andres_lozada.demo.dto.PaginatedResponse;
import com.andres_lozada.demo.dto.PolicyResponse;
import com.andres_lozada.demo.dto.UpdatePolicyRequest;
import com.andres_lozada.demo.entity.CustomerEntity;
import com.andres_lozada.demo.entity.PolicyEntity;
import com.andres_lozada.demo.entity.RiskEntity;
import com.andres_lozada.demo.enums.AuditOperation;
import com.andres_lozada.demo.enums.PolicyStatus;
import com.andres_lozada.demo.exception.PolicyNotFoundException;
import com.andres_lozada.demo.exception.ValidationException;
import com.andres_lozada.demo.mapper.PaginatedMapper;
import com.andres_lozada.demo.mapper.PolicyMapper;
import com.andres_lozada.demo.repository.PolicyRepository;
import com.andres_lozada.demo.repository.RiskRepository;
import com.andres_lozada.demo.repository.specification.PolicySpecifications;
import com.andres_lozada.demo.security.InputSanitizer;
import com.andres_lozada.demo.util.PaginationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final RiskRepository riskRepository;
    private final CustomerService customerService;
    private final AuditService auditService;
    private final PolicyMapper policyMapper;
    private final PaginatedMapper paginatedMapper;
    private final InputSanitizer inputSanitizer;

    @Auditable(entity = "POLICY", operation = AuditOperation.CREATE)
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 20)
    public PolicyResponse createPolicy(CreatePolicyRequest request) {
        request.setPolicyType(inputSanitizer.sanitizeString(request.getPolicyType()));
        request.setDescription(inputSanitizer.sanitizeString(request.getDescription()));
        log.info("Creating new policy for customer: {}", request.getCustomerId());
        CustomerEntity customer = customerService.validateCustomerExists(request.getCustomerId());
        validatePolicyBusinessRules(request.getCoverageAmount(), request.getPremiumAmount(),
                request.getStartDate(), request.getEndDate(), true);

        Set<RiskEntity> risks = resolveRisks(request.getRiskIds());
        PolicyEntity policy = policyMapper.toEntity(request);
        policy.setCustomerId(request.getCustomerId());
        policy.setCustomer(customer);
        policy.setRisks(risks);
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setCreatedBy(auditService.currentUserId());

        PolicyEntity savedPolicy = policyRepository.save(policy);
        log.info("Policy created successfully: {}", savedPolicy.getPolicyId());
        return policyMapper.toResponse(savedPolicy);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public PolicyResponse getPolicy(UUID policyId) {
        PolicyEntity policy = policyRepository.findByIdWithRisks(policyId)
                .orElseThrow(() -> new PolicyNotFoundException("Policy not found: " + policyId));
        return policyMapper.toResponse(policy);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public PaginatedResponse<PolicyResponse> listPolicies(
            UUID customerId,
            String status,
            Integer pageNumber,
            Integer pageSize) {
        PolicyStatus policyStatus = parseStatus(status);
        Page<PolicyEntity> policyPage = policyRepository.findAll(
                PolicySpecifications.withFilters(customerId, policyStatus),
                PaginationHelper.toPageable(pageNumber, pageSize));
        return paginatedMapper.toPaginatedResponse(policyPage, policyMapper::toResponse);
    }

    @Auditable(entity = "POLICY", operation = AuditOperation.UPDATE)
    @Transactional(isolation = Isolation.REPEATABLE_READ, timeout = 15)
    public PolicyResponse updatePolicy(UUID policyId, UpdatePolicyRequest request) {
        request.setDescription(inputSanitizer.sanitizeString(request.getDescription()));
        PolicyEntity existingPolicy = policyRepository.findByIdWithRisks(policyId)
                .orElseThrow(() -> new PolicyNotFoundException("Policy not found: " + policyId));

        if (request.getStatus() != null) {
            existingPolicy.setStatus(request.getStatus());
        }
        if (request.getCoverageAmount() != null) {
            existingPolicy.setCoverageAmount(request.getCoverageAmount());
        }
        if (request.getPremiumAmount() != null) {
            existingPolicy.setPremiumAmount(request.getPremiumAmount());
        }
        if (request.getEndDate() != null) {
            existingPolicy.setEndDate(request.getEndDate());
        }
        if (request.getDescription() != null) {
            existingPolicy.setDescription(request.getDescription());
        }
        if (request.getRiskIds() != null) {
            existingPolicy.setRisks(resolveRisks(request.getRiskIds()));
        }

        validatePolicyBusinessRules(
                existingPolicy.getCoverageAmount(),
                existingPolicy.getPremiumAmount(),
                existingPolicy.getStartDate(),
                existingPolicy.getEndDate(),
                false);

        existingPolicy.setUpdatedBy(auditService.currentUserId());
        PolicyEntity updatedPolicy = policyRepository.save(existingPolicy);
        return policyMapper.toResponse(updatedPolicy);
    }

    @Auditable(entity = "POLICY", operation = AuditOperation.DELETE)
    @Transactional(isolation = Isolation.REPEATABLE_READ, timeout = 15)
    public void deletePolicy(UUID policyId) {
        PolicyEntity existingPolicy = policyRepository.findById(policyId)
                .orElseThrow(() -> new PolicyNotFoundException("Policy not found: " + policyId));
        policyRepository.delete(existingPolicy);
    }

    private void validatePolicyBusinessRules(
            BigDecimal coverageAmount,
            BigDecimal premiumAmount,
            LocalDate startDate,
            LocalDate endDate,
            boolean enforceFutureStart) {
        java.util.List<ValidationException.FieldError> errors = new java.util.ArrayList<>();
        if (coverageAmount == null || coverageAmount.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(new ValidationException.FieldError("coverageAmount", "Coverage amount must be positive"));
        }
        if (premiumAmount == null || premiumAmount.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(new ValidationException.FieldError("premiumAmount", "Premium amount must be positive"));
        }
        if (enforceFutureStart && startDate != null && startDate.isBefore(LocalDate.now())) {
            errors.add(new ValidationException.FieldError("startDate", "Start date must be today or in the future"));
        }
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            errors.add(new ValidationException.FieldError("endDate", "End date cannot be before start date"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Policy validation failed", errors);
        }
    }

    private Set<RiskEntity> resolveRisks(List<UUID> riskIds) {
        Set<RiskEntity> risks = new HashSet<>();
        if (riskIds == null || riskIds.isEmpty()) {
            return risks;
        }
        List<RiskEntity> riskEntities = riskRepository.findAllById(riskIds);
        if (riskEntities.size() != riskIds.size()) {
            Set<UUID> found = riskEntities.stream().map(RiskEntity::getRiskId).collect(Collectors.toSet());
            List<UUID> missing = riskIds.stream().filter(id -> !found.contains(id)).toList();
            throw new ValidationException("Some risks not found: " + missing,
                    List.of(new ValidationException.FieldError("riskIds", "Unknown risk ids: " + missing)));
        }
        risks.addAll(riskEntities);
        return risks;
    }

    private PolicyStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return PolicyStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid policy status: " + status,
                    List.of(new ValidationException.FieldError("status", "Must be ACTIVE, INACTIVE, SUSPENDED or CANCELLED")));
        }
    }
}
