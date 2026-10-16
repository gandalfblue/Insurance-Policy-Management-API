package com.andres_lozada.demo.util;

import com.andres_lozada.demo.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaginationHelperTest {
    @Test
    void defaultsToFirstPageAndTenRows() {
        var pageable = PaginationHelper.toPageable(null, null);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    void convertsOneBasedPageNumber() {
        assertEquals(2, PaginationHelper.toPageable(3, 25).getPageNumber());
    }

    @Test
    void rejectsOutOfRangeValues() {
        assertThrows(ValidationException.class, () -> PaginationHelper.toPageable(0, 10));
        assertThrows(ValidationException.class, () -> PaginationHelper.toPageable(1, 101));
    }
}
