package org.csc311.capstone.db;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.csc311.capstone.models.Staff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {

    public static void initializeTable() throws SQLException {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS staff (
                id VARCHAR(20) PRIMARY KEY,
                role_id INTEGER,
                job_type VARCHAR(100) NOT NULL,
                img_url TEXT,
                email VARCHAR(255) UNIQUE NOT NULL,
                first_name VARCHAR(100) NOT NULL,
                last_name VARCHAR(100) NOT NULL,
                department VARCHAR(100) NOT NULL,
                password_hash TEXT NOT NULL
            )
        """;

        String addRoleIdColumnSql = """
            ALTER TABLE staff
            ADD COLUMN IF NOT EXISTS role_id INTEGER
        """;

        String addImageColumnSql = """
            ALTER TABLE staff
            ADD COLUMN IF NOT EXISTS img_url TEXT
        """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createTableSql);
            stmt.execute(addRoleIdColumnSql);
            stmt.execute(addImageColumnSql);
        }
    }

    public static void seedDefaultAdminIfEmpty() throws SQLException {
        initializeTable();

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
        admin.setImgURL(null);
        admin.setPasswordHash(hashPassword("admin123"));

        insert(admin);
    }

    public static Staff findByEmail(String email) throws SQLException {
        String sql = """
            SELECT s.id,
                   COALESCE(r.name, s.job_type) AS job_type,
                   s.img_url,
                   s.email,
                   s.first_name,
                   s.last_name,
                   s.department,
                   s.password_hash
            FROM staff s
            LEFT JOIN roles r ON s.role_id = r.id
            WHERE LOWER(s.email) = LOWER(?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return mapStaff(rs);
            }
        }
    }

    public static List<Staff> findAll() throws SQLException {
        List<Staff> staffList = new ArrayList<>();

        String sql = """
            SELECT s.id,
                   COALESCE(r.name, s.job_type) AS job_type,
                   s.img_url,
                   s.email,
                   s.first_name,
                   s.last_name,
                   s.department,
                   s.password_hash
            FROM staff s
            LEFT JOIN roles r ON s.role_id = r.id
            ORDER BY s.last_name, s.first_name
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                staffList.add(mapStaff(rs));
            }
        }

        return staffList;
    }

    public static void insert(Staff staff) throws SQLException {
        Integer roleId = findRoleId(staff.getJobType());

        String sql = """
            INSERT INTO staff (
                id,
                role_id,
                job_type,
                img_url,
                email,
                first_name,
                last_name,
                department,
                password_hash
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staff.getID());

            if (roleId == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, roleId);
            }

            stmt.setString(3, staff.getJobType());
            stmt.setString(4, staff.getImgURL());
            stmt.setString(5, staff.getEmail());
            stmt.setString(6, staff.getFirstName());
            stmt.setString(7, staff.getLastName());
            stmt.setString(8, staff.getDepartment());
            stmt.setString(9, staff.getPasswordHash());

            stmt.executeUpdate();
        }
    }

    public static void updateProfileImage(String staffId, String imageUrl) throws SQLException {
        String sql = """
        UPDATE staff
        SET img_url = ?
        WHERE id = ?
    """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, imageUrl);
            stmt.setString(2, staffId);

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

    private static Integer findRoleId(String roleName) throws SQLException {
        if (roleName == null || roleName.trim().isEmpty()) {
            return null;
        }

        ensureRolesTable();

        String insertSql = """
            INSERT INTO roles (name)
            VALUES (?)
            ON CONFLICT (name) DO NOTHING
        """;

        String selectSql = """
            SELECT id
            FROM roles
            WHERE LOWER(name) = LOWER(?)
        """;

        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                insert.setString(1, roleName.trim());
                insert.executeUpdate();
            }

            try (PreparedStatement select = conn.prepareStatement(selectSql)) {
                select.setString(1, roleName.trim());

                try (ResultSet rs = select.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
        }

        return null;
    }

    private static void ensureRolesTable() throws SQLException {
        String createRolesSql = """
            CREATE TABLE IF NOT EXISTS roles (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100) UNIQUE NOT NULL
            )
        """;

        String seedRolesSql = """
            INSERT INTO roles (name)
            VALUES ('Administrator'), ('Staff')
            ON CONFLICT (name) DO NOTHING
        """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createRolesSql);
            stmt.execute(seedRolesSql);
        }
    }

    private static Staff mapStaff(ResultSet rs) throws SQLException {
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