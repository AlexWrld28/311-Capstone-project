package org.csc311.capstone.services;

import org.csc311.capstone.db.AuditLogRepository;
import org.csc311.capstone.db.StaffRepository;
import org.csc311.capstone.models.Staff;
import org.csc311.capstone.storage.MinioStorageService;

import java.io.File;
import java.sql.SQLException;

public class ProfileService {

    public String uploadProfileImage(Staff staff, File file) throws SQLException {
        if (staff == null) {
            throw new IllegalArgumentException("No staff user is logged in.");
        }

        String imageUrl = MinioStorageService.uploadStaffImage(file, staff.getID());

        StaffRepository.updateProfileImage(staff.getID(), imageUrl);
        staff.setImgURL(imageUrl);

        AuditLogRepository.log(
                staff,
                "UPLOAD_PROFILE_IMAGE",
                null,
                "Uploaded staff profile picture: " + imageUrl
        );

        return imageUrl;
    }
}