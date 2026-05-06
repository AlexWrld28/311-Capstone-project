package org.csc311.capstone.services;

import org.csc311.capstone.db.AuditLogRepository;
import org.csc311.capstone.models.AuditLog;
import org.csc311.capstone.models.PaginatedResult;
import org.csc311.capstone.models.Staff;

import java.sql.SQLException;

public class AuditService {

    public PaginatedResult<AuditLog> findPage(Staff currentUser, int page, int pageSize) throws SQLException {
        if (currentUser == null || !isAdmin(currentUser)) {
            throw new SecurityException("Only administrators can view audit logs.");
        }

        return AuditLogRepository.findPage(page, pageSize);
    }

    private boolean isAdmin(Staff staff) {
        return staff.getJobType() != null
                && (staff.getJobType().equalsIgnoreCase("Administrator")
                || staff.getJobType().equalsIgnoreCase("Admin"));
    }
}