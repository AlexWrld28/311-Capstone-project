package org.csc311.capstone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LookupRepository {

    public static List<String> findDepartments() throws SQLException {
        List<String> result = new ArrayList<>();

        String sql = "SELECT name FROM departments ORDER BY name";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(rs.getString("name"));
            }
        }

        return result;
    }

    public static List<String> findMajors() throws SQLException {
        List<String> result = new ArrayList<>();

        String sql = "SELECT name FROM majors ORDER BY name";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(rs.getString("name"));
            }
        }

        return result;
    }

    public static List<String> findRoles() throws SQLException {
        List<String> result = new ArrayList<>();

        String sql = "SELECT name FROM roles ORDER BY name";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(rs.getString("name"));
            }
        }

        return result;
    }
}