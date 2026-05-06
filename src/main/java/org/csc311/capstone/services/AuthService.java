package org.csc311.capstone.services;

import org.csc311.capstone.db.StaffRepository;
import org.csc311.capstone.models.Staff;

import java.sql.SQLException;

public class AuthService {

    public Staff login(String email, String password) throws SQLException {
        Staff user = StaffRepository.findByEmail(email);

        if (user == null || !StaffRepository.verifyPassword(password, user.getPasswordHash())) {
            return null;
        }

        return user;
    }

    public Staff register(String firstName, String lastName, String email, String password, String role) throws SQLException {
        if (StaffRepository.findByEmail(email) != null) {
            return null;
        }

        Staff staff = new Staff();
        staff.setID(StaffRepository.nextStaffId());
        staff.setFirstName(firstName);
        staff.setLastName(lastName);
        staff.setEmail(email);
        staff.setJobType(role);
        staff.setDepartment("Student Services");
        staff.setImgURL(null);
        staff.setPasswordHash(StaffRepository.hashPassword(password));

        StaffRepository.insert(staff);

        return staff;
    }
}