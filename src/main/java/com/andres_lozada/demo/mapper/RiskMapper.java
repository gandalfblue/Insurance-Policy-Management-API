package com.andres_lozada.demo.mapper;

import com.andres_lozada.demo.dto.CreateRiskRequest;
import com.andres_lozada.demo.dto.RiskResponse;
import com.andres_lozada.demo.dto.UpdateRiskRequest;
import com.andres_lozada.demo.entity.RiskEntity;
import org.mapstruct.*;

/**
 * Mapper for Risk entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface RiskMapper {

    /**
     * Maps RiskEntity to RiskResponse.
     *
     * @param entity the entity to map
     * @return the response DTO
     */
    RiskResponse toResponse(RiskEntity entity);

    /**
     * Maps CreateRiskRequest to RiskEntity.
     * Fields like riskId, status, createdAt, updatedAt are not included.
     *
     * @param request the request DTO
     * @return the entity
     */
    @Mapping(target = "riskId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "policies", ignore = true)
    RiskEntity toEntity(CreateRiskRequest request);

    /**
     * Updates RiskEntity from UpdateRiskRequest (partial update).
     *
     * @param request the request DTO
     * @param entity  the entity to update
     * @return the updated entity
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RiskEntity updateFromRequest(UpdateRiskRequest request, @MappingTarget RiskEntity entity);
}
