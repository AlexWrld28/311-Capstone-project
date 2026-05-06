package org.csc311.capstone.models;

import java.sql.Timestamp;

public class AuditLog {
    private int id;
    private String staffId;
    private String staffName;
    private String staffEmail;
    private String staffImgUrl;
    private String action;
    private String studentId;
    private String details;
    private Timestamp createdAt;

    public AuditLog() {
    }

    public AuditLog(int id, String staffId, String staffName, String staffEmail, String staffImgUrl,
                    String action, String studentId, String details, Timestamp createdAt) {
        this.id = id;
        this.staffId = staffId;
        this.staffName = staffName;
        this.staffEmail = staffEmail;
        this.staffImgUrl = staffImgUrl;
        this.action = action;
        this.studentId = studentId;
        this.details = details;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public String getStaffImgUrl() {
        return staffImgUrl;
    }

    public String getAction() {
        return action;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getDetails() {
        return details;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}