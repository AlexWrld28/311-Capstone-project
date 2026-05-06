package org.csc311.capstone.services;

import org.csc311.capstone.db.StudentRepository;
import org.csc311.capstone.models.DashboardStats;

import java.sql.SQLException;

public class DashboardService {

    public DashboardStats getStats() throws SQLException {
        return StudentRepository.getDashboardStats();
    }
}