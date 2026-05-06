package org.csc311.capstone.db;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.csc311.capstone.models.Staff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {

    public static void initializeTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS staff (
                id VARCHAR(20) PRIMARY KEY,
                job_type VARCHAR(100) NOT NULL,
                img_url TEXT,
                email VARCHAR(255) UNIQUE NOT NULL,
                first_name VARCHAR(100) NOT NULL,
                last_name VARCHAR(100) NOT NULL,
                department VARCHAR(100) NOT NULL,
                password_hash TEXT NOT NULL
            )
        """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void seedDefaultAdminIfEmpty() throws SQLException {
        String countSql = "SELECT COUNT(*) FROM staff";

        try (Connection conn = Database.getConnection();
             Statement countStmt = conn.createStatement();
             ResultSet rs = countStmt.executeQuery(countSql)) {

            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        Staff admin = new Staff();
        admin.setID("A001");
        admin.setFirstName("Demo");
        admin.setLastName("Admin");
        admin.setEmail("admin@school.edu");
        admin.setJobType("Administrator");
        admin.setDepartment("Administration");
        admin.setPasswordHash(hashPassword("admin123"));

        insert(admin);
    }

    public static Staff findByEmail(String email) throws SQLException {
        String sql = """
            SELECT id, job_type, img_url, email, first_name, last_name, department, password_hash
            FROM staff
            WHERE LOWER(email) = LOWER(?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Staff staff = new Staff();
                staff.setID(rs.getString("id"));
                staff.setJobType(rs.getString("job_type"));
                staff.setImgURL(rs.getString("img_url"));
                staff.setEmail(rs.getString("email"));
                staff.setFirstName(rs.getString("first_name"));
                staff.setLastName(rs.getString("last_name"));
                staff.setDepartment(rs.getString("department"));
                staff.setPasswordHash(rs.getString("password_hash"));

                return staff;
            }
        }
    }

    public static List<Staff> findAll() throws SQLException {
        List<Staff> staffList = new ArrayList<>();

        String sql = """
            SELECT id, job_type, img_url, email, first_name, last_name, department, password_hash
            FROM staff
            ORDER BY last_name, first_name
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Staff staff = new Staff();
                staff.setID(rs.getString("id"));
                staff.setJobType(rs.getString("job_type"));
                staff.setImgURL(rs.getString("img_url"));
                staff.setEmail(rs.getString("email"));
                staff.setFirstName(rs.getString("first_name"));
                staff.setLastName(rs.getString("last_name"));
                staff.setDepartment(rs.getString("department"));
                staff.setPasswordHash(rs.getString("password_hash"));

                staffList.add(staff);
            }
        }

        return staffList;
    }

    public static void insert(Staff staff) throws SQLException {
        String sql = """
            INSERT INTO staff (id, job_type, img_url, email, first_name, last_name, department, password_hash)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staff.getID());
            stmt.setString(2, staff.getJobType());
            stmt.setString(3, staff.getImgURL());
            stmt.setString(4, staff.getEmail());
            stmt.setString(5, staff.getFirstName());
            stmt.setString(6, staff.getLastName());
            stmt.setString(7, staff.getDepartment());
            stmt.setString(8, staff.getPasswordHash());

            stmt.executeUpdate();
        }
    }

    public static String nextStaffId() throws SQLException {
        String sql = "SELECT COUNT(*) FROM staff";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            rs.next();
            return "S%03d".formatted(rs.getInt(1) + 1);
        }
    }

    public static String hashPassword(String password) {
        return BCrypt.withDefaults()
                .hashToString(12, password.toCharArray());
    }

    public static boolean verifyPassword(String password, String passwordHash) {
        if (password == null || passwordHash == null) {
            return false;
        }

        return BCrypt.verifyer()
                .verify(password.toCharArray(), passwordHash)
                .verified;
    }
}