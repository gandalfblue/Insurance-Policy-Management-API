package com.andres_lozada.demo.mapper;

import com.andres_lozada.demo.dto.CreateCustomerRequest;
import com.andres_lozada.demo.dto.CustomerResponse;
import com.andres_lozada.demo.dto.UpdateCustomerRequest;
import com.andres_lozada.demo.entity.CustomerEntity;
import org.mapstruct.*;

/**
 * Mapper for Customer entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper {

    /**
     * Maps CustomerEntity to CustomerResponse.
     *
     * @param entity the entity to map
     * @return the response DTO
     */
    CustomerResponse toResponse(CustomerEntity entity);

    /**
     * Maps CreateCustomerRequest to CustomerEntity.
     * Fields like customerId, createdAt, updatedAt are not included.
     *
     * @param request the request DTO
     * @return the entity
     */
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "policies", ignore = true)
    CustomerEntity toEntity(CreateCustomerRequest request);

    /**
     * Updates CustomerEntity from UpdateCustomerRequest (partial update).
     *
     * @param request the request DTO
     * @param entity  the entity to update
     * @return the updated entity
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CustomerEntity updateFromRequest(UpdateCustomerRequest request, @MappingTarget CustomerEntity entity);
}
