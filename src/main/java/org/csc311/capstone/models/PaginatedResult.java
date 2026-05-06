package org.csc311.capstone.models;

import java.util.List;

public record PaginatedResult<T>(
        List<T> items,
        int totalItems,
        int page,
        int pageSize
) {
    public int totalPages() {
        if (pageSize <= 0) {
            return 1;
        }

        return Math.max(1, (int) Math.ceil((double) totalItems / pageSize));
    }
}