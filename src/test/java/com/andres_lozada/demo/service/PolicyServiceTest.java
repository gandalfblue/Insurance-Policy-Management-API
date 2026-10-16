package com.andres_lozada.demo.service;

import com.andres_lozada.demo.dto.CreatePolicyRequest;
import com.andres_lozada.demo.dto.PolicyResponse;
import com.andres_lozada.demo.dto.UpdatePolicyRequest;
import com.andres_lozada.demo.entity.CustomerEntity;
import com.andres_lozada.demo.entity.PolicyEntity;
import com.andres_lozada.demo.exception.ValidationException;
import com.andres_lozada.demo.mapper.PaginatedMapper;
import com.andres_lozada.demo.mapper.PolicyMapper;
import com.andres_lozada.demo.repository.PolicyRepository;
import com.andres_lozada.demo.repository.RiskRepository;
import com.andres_lozada.demo.security.InputSanitizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {
    @Mock PolicyRepository policyRepository;
    @Mock RiskRepository riskRepository;
    @Mock CustomerService customerService;
    @Mock AuditService auditService;
    @Mock PolicyMapper policyMapper;
    @Mock PaginatedMapper paginatedMapper;
    @Mock InputSanitizer inputSanitizer;
    @InjectMocks PolicyService policyService;

    @Test
    void createsPolicyForExistingCustomer() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = new CustomerEntity();
        PolicyEntity entity = new PolicyEntity();
        var request = CreatePolicyRequest.builder().customerId(customerId).policyType("AUTO")
                .coverageAmount(new BigDecimal("1000")).premiumAmount(new BigDecimal("50"))
                .startDate(LocalDate.now().plusDays(1)).build();
        when(inputSanitizer.sanitizeString("AUTO")).thenReturn("AUTO");
        when(inputSanitizer.sanitizeString(null)).thenReturn(null);
        when(customerService.validateCustomerExists(customerId)).thenReturn(customer);
        when(policyMapper.toEntity(request)).thenReturn(entity);
        when(policyRepository.save(entity)).thenReturn(entity);
        when(policyMapper.toResponse(entity)).thenReturn(com.andres_lozada.demo.dto.PolicyResponse.builder().build());

        assertNotNull(policyService.createPolicy(request));
        assertEquals(customer, entity.getCustomer());
        assertEquals(customerId, entity.getCustomerId());
        verify(policyRepository).save(entity);
    }

    @Test
    void rejectsNonPositiveCoverage() {
        var request = CreatePolicyRequest.builder().customerId(UUID.randomUUID()).policyType("AUTO")
                .coverageAmount(BigDecimal.ZERO).premiumAmount(BigDecimal.ONE)
                .startDate(LocalDate.now()).build();
        when(inputSanitizer.sanitizeString("AUTO")).thenReturn("AUTO");
        when(inputSanitizer.sanitizeString(null)).thenReturn(null);
        when(customerService.validateCustomerExists(request.getCustomerId())).thenReturn(new CustomerEntity());
        assertThrows(ValidationException.class, () -> policyService.createPolicy(request));
        verify(policyRepository, never()).save(any());
    }

    @Test
    void getPolicyThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(policyRepository.findByIdWithRisks(id)).thenReturn(Optional.empty());
        assertThrows(com.andres_lozada.demo.exception.PolicyNotFoundException.class,
                () -> policyService.getPolicy(id));
    }

    @Test
    void rejectsEndDateBeforeStartDate() {
        var request = CreatePolicyRequest.builder().customerId(UUID.randomUUID()).policyType("AUTO")
                .coverageAmount(BigDecimal.ONE).premiumAmount(BigDecimal.ONE)
                .startDate(LocalDate.now().plusDays(2)).endDate(LocalDate.now().plusDays(1)).build();
        when(inputSanitizer.sanitizeString("AUTO")).thenReturn("AUTO");
        when(inputSanitizer.sanitizeString(null)).thenReturn(null);
        when(customerService.validateCustomerExists(request.getCustomerId())).thenReturn(new CustomerEntity());
        assertThrows(ValidationException.class, () -> policyService.createPolicy(request));
    }

    @Test
    void rejectsUnknownRiskIds() {
        UUID riskId = UUID.randomUUID();
        var request = CreatePolicyRequest.builder().customerId(UUID.randomUUID()).policyType("AUTO")
                .coverageAmount(BigDecimal.ONE).premiumAmount(BigDecimal.ONE)
                .startDate(LocalDate.now().plusDays(1)).riskIds(List.of(riskId)).build();
        when(inputSanitizer.sanitizeString("AUTO")).thenReturn("AUTO");
        when(inputSanitizer.sanitizeString(null)).thenReturn(null);
        when(customerService.validateCustomerExists(request.getCustomerId())).thenReturn(new CustomerEntity());
        when(riskRepository.findAllById(List.of(riskId))).thenReturn(List.of());
        assertThrows(ValidationException.class, () -> policyService.createPolicy(request));
    }

    @Test
    void rejectsInvalidPolicyStatusFilter() {
        assertThrows(ValidationException.class, () -> policyService.listPolicies(null, "unknown", 1, 10));
    }

    @Test
    void updatesPolicyFields() {
        UUID policyId = UUID.randomUUID();
        PolicyEntity entity = PolicyEntity.builder().policyId(policyId).startDate(LocalDate.now().plusDays(1))
                .coverageAmount(BigDecimal.TEN).premiumAmount(BigDecimal.ONE).build();
        UpdatePolicyRequest update = UpdatePolicyRequest.builder().coverageAmount(new BigDecimal("200"))
                .description("updated").build();
        PolicyResponse response = PolicyResponse.builder().policyId(policyId).build();
        when(inputSanitizer.sanitizeString("updated")).thenReturn("updated");
        when(policyRepository.findByIdWithRisks(policyId)).thenReturn(Optional.of(entity));
        when(auditService.currentUserId()).thenReturn("test-user");
        when(policyRepository.save(entity)).thenReturn(entity);
        when(policyMapper.toResponse(entity)).thenReturn(response);

        assertEquals(response, policyService.updatePolicy(policyId, update));
        assertEquals(new BigDecimal("200"), entity.getCoverageAmount());
        assertEquals("updated", entity.getDescription());
    }
}
