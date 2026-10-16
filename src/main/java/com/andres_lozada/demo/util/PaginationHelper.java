package com.andres_lozada.demo.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.andres_lozada.demo.exception.ValidationException;

/**
 * Converts 1-indexed API pagination parameters to Spring Data {@link Pageable}.
 */
public final class PaginationHelper {

    public static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    private PaginationHelper() {
    }

    public static Pageable toPageable(Integer pageNumber, Integer pageSize) {
        return toPageable(pageNumber, pageSize, Sort.by("createdAt").descending());
    }

    public static Pageable toPageable(Integer pageNumber, Integer pageSize, Sort sort) {
        int page = pageNumber == null ? DEFAULT_PAGE_NUMBER : pageNumber;
        int size = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
        if (page < 1) {
            throw new ValidationException("pageNumber must be at least 1");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new ValidationException("pageSize must be between 1 and " + MAX_PAGE_SIZE);
        }
        return PageRequest.of(page - 1, size, sort);
    }
}
