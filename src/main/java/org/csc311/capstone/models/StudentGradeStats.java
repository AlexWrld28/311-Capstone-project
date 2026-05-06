package org.csc311.capstone.models;

public record StudentGradeStats(
        int classCount,
        int totalCredits,
        double averageGrade,
        double highestGrade,
        double lowestGrade
) {
}
