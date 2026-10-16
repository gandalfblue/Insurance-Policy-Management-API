package com.andres_lozada.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.data.domain.Page;

import java.util.function.Function;

/**
 * Generic mapper for paginated responses.
 * 
 * Usage example:
 * <pre>
 * {@code
 * Page<PolicyEntity> entityPage = policyRepository.findAll(pageable);
 * PaginatedResponse<PolicyResponse> response = paginatedMapper.toPaginatedResponse(
 *     entityPage,
 *     policyMapper::toResponse
 * );
 * }
 * </pre>
 */
@Mapper(componentModel = "spring")
public interface PaginatedMapper {

    /**
     * Converts a Page of entities to a PaginatedResponse of DTOs.
     *
     * @param <T>          the source entity type
     * @param <R>          the target DTO type
     * @param page         the page of entities
     * @param mapper       the function to map entity to DTO
     * @return the paginated response
     */
    default <T, R> com.andres_lozada.demo.dto.PaginatedResponse<R> toPaginatedResponse(
            Page<T> page,
            Function<T, R> mapper) {
        
        java.util.List<R> content = page.getContent().stream()
            .map(mapper)
            .toList();
        
        return com.andres_lozada.demo.dto.PaginatedResponse.<R>builder()
            .data(content)
            .pagination(com.andres_lozada.demo.dto.PaginatedResponse.Pagination.builder()
                .currentPage(page.getNumber() + 1) // page.getNumber() is 0-indexed
                .pageSize(page.getSize())
                .totalCount(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build())
            .build();
    }
}
