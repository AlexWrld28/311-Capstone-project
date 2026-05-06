package org.csc311.capstone.models;

import java.util.Map;

public record DashboardStats(
        int totalStudents,
        double averageGpa,
        double highestGpa,
        double lowestGpa,
        Map<String, Integer> studentsByDepartment
) {
}