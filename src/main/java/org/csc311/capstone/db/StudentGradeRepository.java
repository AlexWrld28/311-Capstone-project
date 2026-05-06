package org.csc311.capstone.db;

import org.csc311.capstone.models.StudentGrade;
import org.csc311.capstone.models.StudentGradeStats;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentGradeRepository {

    public static List<StudentGrade> findByStudentId(String studentId) throws SQLException {
        String sql = """
            SELECT id, student_id, class_name, class_code, grade, credits, term
            FROM student_class_grades
            WHERE student_id = ?
            ORDER BY term DESC, class_code ASC, class_name ASC
        """;

        List<StudentGrade> grades = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapGrade(rs));
                }
            }
        }

        return grades;
    }

    public static void insert(StudentGrade grade) throws SQLException {
        String sql = """
            INSERT INTO student_class_grades (student_id, class_name, class_code, grade, credits, term)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            bindEditableFields(stmt, grade);
            stmt.executeUpdate();
        }
    }

    public static void update(StudentGrade grade) throws SQLException {
        String sql = """
            UPDATE student_class_grades
            SET student_id = ?,
                class_name = ?,
                class_code = ?,
                grade = ?,
                credits = ?,
                term = ?
            WHERE id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            bindEditableFields(stmt, grade);
            stmt.setInt(7, grade.getId());
            stmt.executeUpdate();
        }
    }

    public static void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM student_class_grades WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static StudentGradeStats getStatsForStudent(String studentId) throws SQLException {
        String sql = """
            SELECT COUNT(*) AS class_count,
                   COALESCE(SUM(credits), 0) AS total_credits,
                   COALESCE(AVG(grade), 0) AS average_grade,
                   COALESCE(MAX(grade), 0) AS highest_grade,
                   COALESCE(MIN(grade), 0) AS lowest_grade
            FROM student_class_grades
            WHERE student_id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new StudentGradeStats(
                            rs.getInt("class_count"),
                            rs.getInt("total_credits"),
                            rs.getDouble("average_grade"),
                            rs.getDouble("highest_grade"),
                            rs.getDouble("lowest_grade")
                    );
                }
            }
        }

        return new StudentGradeStats(0, 0, 0, 0, 0);
    }

    private static void bindEditableFields(PreparedStatement stmt, StudentGrade grade) throws SQLException {
        stmt.setString(1, grade.getStudentId());
        stmt.setString(2, grade.getClassName());
        stmt.setString(3, grade.getClassCode());
        stmt.setBigDecimal(4, grade.getGrade());
        stmt.setInt(5, grade.getCredits());
        stmt.setString(6, grade.getTerm());
    }

    private static StudentGrade mapGrade(ResultSet rs) throws SQLException {
        BigDecimal grade = rs.getBigDecimal("grade");

        return new StudentGrade(
                rs.getInt("id"),
                rs.getString("student_id"),
                rs.getString("class_name"),
                rs.getString("class_code"),
                grade,
                rs.getInt("credits"),
                rs.getString("term")
        );
    }
}
