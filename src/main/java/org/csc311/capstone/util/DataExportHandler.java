package org.csc311.capstone.util;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.csc311.capstone.models.Student;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * The DataExportHandler class provides functionality to export data in different formats.
 * It currently supports exporting data to CSV and PDF files.
 */
public class DataExportHandler {

    public static void exportToCSV(List<Student> studentList, String fileName) {
        try (var writer = new FileWriter(fileName + ".csv")) {
            StatefulBeanToCsv<Student> beanToCsv = new StatefulBeanToCsvBuilder<Student>(writer)
                    .withSeparator(',')// Optional: define custom delimiter
                    .build();
            beanToCsv.write(studentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public static void exportToPDF(){};
}
