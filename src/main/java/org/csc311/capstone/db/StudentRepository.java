package org.csc311.capstone.db;

import org.csc311.capstone.models.Student;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    public static void initializeTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS students (
                id VARCHAR(20) PRIMARY KEY,
                first_name VARCHAR(100) NOT NULL,
                last_name VARCHAR(100) NOT NULL,
                department VARCHAR(100) NOT NULL,
                major VARCHAR(100) NOT NULL,
                gpa NUMERIC(3,2) NOT NULL
            )
        """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static List<Student> findAll() throws SQLException {
        List<Student> students = new ArrayList<>();

        String sql = """
            SELECT id, first_name, last_name, department, major, gpa
            FROM students
            ORDER BY id
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                students.add(new Student(
                        rs.getString("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("department"),
                        rs.getString("major"),
                        rs.getBigDecimal("gpa").toPlainString()
                ));
            }
        }

        return students;
    }

    public static void insert(Student student) throws SQLException {
        String sql = """
            INSERT INTO students (id, first_name, last_name, department, major, gpa)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getID());
            stmt.setString(2, student.getFirstName());
            stmt.setString(3, student.getLastName());
            stmt.setString(4, student.getDepartment());
            stmt.setString(5, student.getMajor());
            stmt.setBigDecimal(6, new BigDecimal(student.getGpa()));

            stmt.executeUpdate();
        }
    }

    public static void update(Student student) throws SQLException {
        String sql = """
            UPDATE students
            SET first_name = ?, last_name = ?, department = ?, major = ?, gpa = ?
            WHERE id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getDepartment());
            stmt.setString(4, student.getMajor());
            stmt.setBigDecimal(5, new BigDecimal(student.getGpa()));
            stmt.setString(6, student.getID());

            stmt.executeUpdate();
        }
    }

    public static void deleteById(String id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }
}