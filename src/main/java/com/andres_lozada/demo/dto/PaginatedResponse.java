package com.andres_lozada.demo.dto;

import lombok.*;

import java.util.List;

/**
 * Generic paginated response wrapper.
 *
 * @param <T> the type of items in the data list
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {

    private List<T> data;
    private Pagination pagination;

    /**
     * Pagination metadata.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int currentPage;
        private int pageSize;
        private long totalCount;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
    }
}
