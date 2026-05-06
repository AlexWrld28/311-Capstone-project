package org.csc311.capstone.db;

import org.csc311.capstone.models.AuditLog;
import org.csc311.capstone.models.PaginatedResult;
import org.csc311.capstone.models.Staff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogRepository {

    public static void log(Staff staff, String action, String studentId, String details) throws SQLException {
        String sql = """
            INSERT INTO audit_logs (
                staff_id,
                staff_name,
                staff_email,
                staff_img_url,
                action,
                student_id,
                details
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (staff == null) {
                stmt.setString(1, null);
                stmt.setString(2, "Unknown");
                stmt.setString(3, null);
                stmt.setString(4, null);
            } else {
                stmt.setString(1, staff.getID());
                stmt.setString(2, staff.getFirstName() + " " + staff.getLastName());
                stmt.setString(3, staff.getEmail());
                stmt.setString(4, staff.getImgURL());
            }

            stmt.setString(5, action);
            stmt.setString(6, studentId);
            stmt.setString(7, details);

            stmt.executeUpdate();
        }
    }

    public static PaginatedResult<AuditLog> findPage(int page, int pageSize) throws SQLException {
        int safePage = Math.max(1, page);
        int safePageSize = Math.max(1, pageSize);
        int offset = (safePage - 1) * safePageSize;

        int totalItems;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM audit_logs");
             ResultSet rs = stmt.executeQuery()) {

            rs.next();
            totalItems = rs.getInt(1);
        }

        List<AuditLog> logs = new ArrayList<>();

        String sql = """
            SELECT id,
                   staff_id,
                   staff_name,
                   staff_email,
                   staff_img_url,
                   action,
                   student_id,
                   details,
                   created_at
            FROM audit_logs
            ORDER BY created_at DESC, id DESC
            LIMIT ?
            OFFSET ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, safePageSize);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new AuditLog(
                            rs.getInt("id"),
                            rs.getString("staff_id"),
                            rs.getString("staff_name"),
                            rs.getString("staff_email"),
                            rs.getString("staff_img_url"),
                            rs.getString("action"),
                            rs.getString("student_id"),
                            rs.getString("details"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        }

        return new PaginatedResult<>(logs, totalItems, safePage, safePageSize);
    }
}