package org.csc311.capstone.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.csc311.capstone.models.Student;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;

/**
 * The DataExportHandler class provides functionality to export data in different formats.
 * It currently supports exporting data to CSV and PDF files.
 */
public class DataExportHandler {

    public static void exportToCSV(List<Student> studentList, String fileName) {
        try (var writer = new FileWriter(fileName + ".csv")) {
            StatefulBeanToCsv<Student> beanToCsv = new StatefulBeanToCsvBuilder<Student>(writer)
                    .withSeparator(',')
                    .build();
            beanToCsv.write(studentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exports a list of students to a PDF file with a formatted table.
     *
     * @param studentList list of students to export
     * @param fileName    output file name without extension
     */
    public static void exportToPDF(List<Student> studentList, String fileName) {
        var document = new Document(PageSize.A4.rotate());
        try {
            // PdfWriter takes ownership of the stream; document.close() will flush and close it
            PdfWriter.getInstance(document, new FileOutputStream(fileName + ".pdf"));
            document.open();

            var titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            var title = new Paragraph("Student Report", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(16);
            document.add(title);

            var table = new PdfPTable(new float[]{1.5f, 2f, 2f, 3f, 3f, 1f});
            table.setWidthPercentage(100);

            var headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            for (var header : new String[]{"ID", "First Name", "Last Name", "Department", "Major", "GPA"}) {
                var cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(70, 130, 180));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            var rowFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            var altRow = new Color(235, 245, 255);
            for (int i = 0; i < studentList.size(); i++) {
                var s = studentList.get(i);
                var bg = (i % 2 == 0) ? Color.WHITE : altRow;
                for (var value : new String[]{s.getID(), s.getFirstName(), s.getLastName(), s.getDepartment(), s.getMajor(), s.getGpa()}) {
                    var cell = new PdfPCell(new Phrase(value != null ? value : "", rowFont));
                    cell.setBackgroundColor(bg);
                    cell.setPadding(4);
                    table.addCell(cell);
                }
            }

            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
