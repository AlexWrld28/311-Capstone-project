package org.csc311.capstone.util;

import org.csc311.capstone.models.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.csc311.capstone.util.DataExportHandler.exportToCSV;
import static org.csc311.capstone.util.DataExportHandler.exportToPDF;
import static org.junit.jupiter.api.Assertions.*;

class DataExportHandlerTest {

    private static final String CSV_FILE = "test";
    private static final String PDF_FILE = "test_students";

    @AfterEach
    void cleanup() {
        new File(CSV_FILE + ".csv").delete();
        new File(PDF_FILE + ".pdf").delete();
    }

    private ArrayList<Student> buildStudents(int count) {
        var students = new ArrayList<Student>();
        for (int i = 0; i < count; i++) {
            var s = new Student();
            s.setID("ID:" + i);
            s.setFirstName("First" + i);
            s.setLastName("Last" + i);
            s.setDepartment("Department" + i);
            s.setMajor("Major" + i);
            s.setGpa(String.valueOf(2.0 + (i * 0.1)));
            students.add(s);
        }
        return students;
    }

    @Test
    void exportToCSVTest() {
        exportToCSV(buildStudents(10), CSV_FILE);
        var file = new File(CSV_FILE + ".csv");
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void exportToPDFCreatesFile() {
        exportToPDF(buildStudents(10), PDF_FILE);
        var file = new File(PDF_FILE + ".pdf");
        assertTrue(file.exists(), "PDF file should be created");
        assertTrue(file.length() > 0, "PDF file should not be empty");
    }

    @Test
    void exportToPDFIsValidPDF() throws IOException {
        exportToPDF(buildStudents(10), PDF_FILE);
        var bytes = Files.readAllBytes(new File(PDF_FILE + ".pdf").toPath());
        // all valid PDFs start with the %PDF magic header
        assertEquals('%', (char) bytes[0]);
        assertEquals('P', (char) bytes[1]);
        assertEquals('D', (char) bytes[2]);
        assertEquals('F', (char) bytes[3]);
    }

    @Test
    void exportToPDFWithEmptyList() {
        exportToPDF(new ArrayList<>(), PDF_FILE);
        var file = new File(PDF_FILE + ".pdf");
        assertTrue(file.exists(), "PDF should be created even for an empty student list");
        assertTrue(file.length() > 0);
    }
}
