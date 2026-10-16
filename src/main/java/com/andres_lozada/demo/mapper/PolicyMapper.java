package com.andres_lozada.demo.mapper;

import com.andres_lozada.demo.dto.CreatePolicyRequest;
import com.andres_lozada.demo.dto.PolicyResponse;
import com.andres_lozada.demo.dto.UpdatePolicyRequest;
import com.andres_lozada.demo.entity.PolicyEntity;
import org.mapstruct.*;

/**
 * Mapper for Policy entities and DTOs.
 */
@Mapper(componentModel = "spring", uses = {RiskMapper.class})
public interface PolicyMapper {

    /**
     * Maps PolicyEntity to PolicyResponse.
     * Includes nested Risk objects.
     *
     * @param entity the entity to map
     * @return the response DTO
     */
    PolicyResponse toResponse(PolicyEntity entity);

    /**
     * Maps CreatePolicyRequest to PolicyEntity (partial mapping).
     * Fields like policyId, status, createdAt, updatedAt, risks are not included.
     *
     * @param request the request DTO
     * @return the entity
     */
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "risks", ignore = true)
    @Mapping(target = "customer", ignore = true)
    PolicyEntity toEntity(CreatePolicyRequest request);

    /**
     * Updates PolicyEntity from UpdatePolicyRequest (partial update).
     *
     * @param request the request DTO
     * @param entity  the entity to update
     * @return the updated entity
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PolicyEntity updateFromRequest(UpdatePolicyRequest request, @MappingTarget PolicyEntity entity);
}
