package org.csc311.capstone.models;

public record StudentSearchCriteria(
        String search,
        String department,
        String major,
        Double minGpa,
        Double maxGpa,
        String sortBy,
        boolean ascending,
        int page,
        int pageSize
) {
}