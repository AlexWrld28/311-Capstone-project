package org.csc311.capstone.util;

import org.csc311.capstone.models.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.csc311.capstone.util.DataExportHandler.exportToCSV;
import static org.junit.jupiter.api.Assertions.*;

class DataExportHandlerTest {

    @Test
    void exportToCSVTest() {
        var students = new ArrayList<Student>();
        for(int i = 0;i<10;i++){
            var newUser = new Student();
            newUser.setID("ID:" + i);
            newUser.setFirstName("firstname"+i);
            newUser.setLastName("lastname"+i);
            newUser.setDepartment("department"+i);
            newUser.setMajor("major"+i);
            newUser.setGpa("gpa"+i);
            students.add(newUser);
        }
        exportToCSV(students,"test");
    }

    @Test
    void exportToPDFTest() {
    }
}