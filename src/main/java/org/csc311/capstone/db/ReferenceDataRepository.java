package org.csc311.capstone.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReferenceDataRepository {

    public static List<String> findDepartments() throws SQLException {
        return findNames("SELECT name FROM departments ORDER BY name");
    }

    public static List<String> findMajors() throws SQLException {
        return findNames("SELECT name FROM majors ORDER BY name");
    }

    public static List<String> findRoles() throws SQLException {
        return findNames("SELECT name FROM roles ORDER BY name");
    }

    public static Integer findOrCreateDepartmentId(String name) throws SQLException {
        return findOrCreateId("departments", name);
    }

    public static Integer findOrCreateMajorId(String name) throws SQLException {
        return findOrCreateId("majors", name);
    }

    public static Integer findRoleId(String roleName) throws SQLException {
        String sql = "SELECT id FROM roles WHERE LOWER(name) = LOWER(?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return null;
    }

    private static List<String> findNames(String sql) throws SQLException {
        List<String> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(rs.getString("name"));
            }
        }

        return result;
    }

    private static Integer findOrCreateId(String table, String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        String insertSql = "INSERT INTO " + table + " (name) VALUES (?) ON CONFLICT (name) DO NOTHING";
        String selectSql = "SELECT id FROM " + table + " WHERE LOWER(name) = LOWER(?)";

        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                insert.setString(1, name.trim());
                insert.executeUpdate();
            }

            try (PreparedStatement select = conn.prepareStatement(selectSql)) {
                select.setString(1, name.trim());

                try (ResultSet rs = select.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
        }

        return null;
    }
}