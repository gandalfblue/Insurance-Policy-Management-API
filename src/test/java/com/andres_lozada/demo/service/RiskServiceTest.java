package com.andres_lozada.demo.service;

import com.andres_lozada.demo.dto.CreateRiskRequest;
import com.andres_lozada.demo.dto.RiskResponse;
import com.andres_lozada.demo.dto.UpdateRiskRequest;
import com.andres_lozada.demo.entity.RiskEntity;
import com.andres_lozada.demo.enums.RiskCategory;
import com.andres_lozada.demo.enums.RiskSeverity;
import com.andres_lozada.demo.exception.RiskNotFoundException;
import com.andres_lozada.demo.exception.ValidationException;
import com.andres_lozada.demo.mapper.PaginatedMapper;
import com.andres_lozada.demo.mapper.RiskMapper;
import com.andres_lozada.demo.repository.RiskRepository;
import com.andres_lozada.demo.security.InputSanitizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskServiceTest {
    @Mock RiskRepository riskRepository;
    @Mock RiskMapper riskMapper;
    @Mock AuditService auditService;
    @Mock PaginatedMapper paginatedMapper;
    @Mock InputSanitizer inputSanitizer;
    @InjectMocks RiskService riskService;

    @Test
    void rejectsDuplicateRiskName() {
        var request = CreateRiskRequest.builder().riskName("Flood").description("Flood exposure")
                .riskSeverity(RiskSeverity.HIGH).build();
        when(inputSanitizer.sanitizeString("Flood")).thenReturn("Flood");
        when(inputSanitizer.sanitizeString("Flood exposure")).thenReturn("Flood exposure");
        when(riskRepository.findByRiskName("Flood")).thenReturn(Optional.of(new com.andres_lozada.demo.entity.RiskEntity()));
        assertThrows(ValidationException.class, () -> riskService.createRisk(request));
    }

    @Test
    void rejectsBlankDescription() {
        var request = CreateRiskRequest.builder().riskName("Flood").description(" ").build();
        when(inputSanitizer.sanitizeString("Flood")).thenReturn("Flood");
        when(inputSanitizer.sanitizeString(" ")).thenReturn("");
        assertThrows(ValidationException.class, () -> riskService.createRisk(request));
    }

    @Test
    void rejectsMissingSeverity() {
        var request = CreateRiskRequest.builder().riskName("Flood").description("Flood exposure").build();
        when(inputSanitizer.sanitizeString("Flood")).thenReturn("Flood");
        when(inputSanitizer.sanitizeString("Flood exposure")).thenReturn("Flood exposure");
        assertThrows(ValidationException.class, () -> riskService.createRisk(request));
        verify(riskRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createsRiskWithValidRequest() {
        var request = CreateRiskRequest.builder().riskName("Wind").riskCategory(RiskCategory.WINDSTORM)
                .riskSeverity(RiskSeverity.MEDIUM).description("Wind exposure").build();
        RiskEntity entity = new RiskEntity();
        when(inputSanitizer.sanitizeString("Wind")).thenReturn("Wind");
        when(inputSanitizer.sanitizeString("Wind exposure")).thenReturn("Wind exposure");
        when(riskRepository.findByRiskName("Wind")).thenReturn(Optional.empty());
        when(riskMapper.toEntity(request)).thenReturn(entity);
        when(riskRepository.save(entity)).thenReturn(entity);
        when(riskMapper.toResponse(entity)).thenReturn(RiskResponse.builder().riskName("Wind").build());
        assertEquals("Wind", riskService.createRisk(request).getRiskName());
        verify(riskRepository).save(entity);
    }

    @Test
    void getsExistingRisk() {
        UUID riskId = UUID.randomUUID();
        RiskEntity entity = new RiskEntity();
        when(riskRepository.findById(riskId)).thenReturn(Optional.of(entity));
        when(riskMapper.toResponse(entity)).thenReturn(RiskResponse.builder().riskName("Flood").build());
        assertEquals("Flood", riskService.getRisk(riskId).getRiskName());
    }

    @Test
    void throwsForMissingRisk() {
        UUID riskId = UUID.randomUUID();
        when(riskRepository.findById(riskId)).thenReturn(Optional.empty());
        assertThrows(RiskNotFoundException.class, () -> riskService.getRisk(riskId));
    }

    @Test
    void rejectsInvalidRiskFilter() {
        assertThrows(ValidationException.class, () -> riskService.listRisks("bad", null, null, 1, 10));
    }

    @Test
    void updatesRiskDescription() {
        UUID riskId = UUID.randomUUID();
        RiskEntity entity = RiskEntity.builder().riskName("Flood").riskSeverity(RiskSeverity.HIGH)
                .description("Old").build();
        UpdateRiskRequest update = UpdateRiskRequest.builder().description("New description").build();
        when(inputSanitizer.sanitizeString(null)).thenReturn(null);
        when(inputSanitizer.sanitizeString("New description")).thenReturn("New description");
        when(riskRepository.findById(riskId)).thenReturn(Optional.of(entity));
        when(riskRepository.save(entity)).thenReturn(entity);
        when(riskMapper.toResponse(entity)).thenReturn(RiskResponse.builder().description("New description").build());
        assertEquals("New description", riskService.updateRisk(riskId, update).getDescription());
        assertEquals("New description", entity.getDescription());
    }

    @Test
    void deletesExistingRisk() {
        UUID riskId = UUID.randomUUID();
        RiskEntity entity = new RiskEntity();
        when(riskRepository.findById(riskId)).thenReturn(Optional.of(entity));
        riskService.deleteRisk(riskId);
        verify(riskRepository).delete(entity);
    }
}
