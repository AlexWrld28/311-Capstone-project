package org.csc311.capstone.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.csc311.capstone.models.Student;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class DataExportHandler {

    public static void exportToCSV(List<Student> studentList, File file) {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write("ID,First Name,Last Name,Department,Major,GPA");
            writer.newLine();

            for (Student student : studentList) {
                writer.write(csv(student.getID()) + ","
                        + csv(student.getFirstName()) + ","
                        + csv(student.getLastName()) + ","
                        + csv(student.getDepartment()) + ","
                        + csv(student.getMajor()) + ","
                        + csv(student.getGpa()));
                writer.newLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to export CSV", e);
        }
    }

    public static void exportToPDF(List<Student> studentList, File file) {
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Student Report", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(16);
            document.add(title);

            PdfPTable table = new PdfPTable(new float[]{1.5f, 2f, 2f, 3f, 3f, 1f});
            table.setWidthPercentage(100);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

            for (String header : new String[]{"ID", "First Name", "Last Name", "Department", "Major", "GPA"}) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(70, 130, 180));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Color altRow = new Color(235, 245, 255);

            for (int i = 0; i < studentList.size(); i++) {
                Student student = studentList.get(i);
                Color background = (i % 2 == 0) ? Color.WHITE : altRow;

                addPdfCell(table, student.getID(), rowFont, background);
                addPdfCell(table, student.getFirstName(), rowFont, background);
                addPdfCell(table, student.getLastName(), rowFont, background);
                addPdfCell(table, student.getDepartment(), rowFont, background);
                addPdfCell(table, student.getMajor(), rowFont, background);
                addPdfCell(table, student.getGpa(), rowFont, background);
            }

            document.add(table);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export PDF", e);
        } finally {
            document.close();
        }
    }

    private static void addPdfCell(PdfPTable table, String value, Font font, Color background) {
        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "", font));
        cell.setBackgroundColor(background);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private static String csv(String value) {
        String escaped = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}