package com.project.sms.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.project.sms.dto.AttendanceSummaryResponse;
import com.project.sms.dto.MarksResponse;
import com.project.sms.dto.MarksSummaryResponse;
import com.project.sms.entity.Student;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final StudentService studentService;
    private final MarksService marksService;
    private final AttendanceService attendanceService;

    public ReportService(StudentService studentService, MarksService marksService,
                         AttendanceService attendanceService) {
        this.studentService = studentService;
        this.marksService = marksService;
        this.attendanceService = attendanceService;
    }

    @Transactional(readOnly = true)
    public byte[] generateMyReport(String studentEmail) {
        Student student = studentService.getStudentByEmail(studentEmail);
        MarksSummaryResponse marks = marksService.getMyMarks(studentEmail);
        AttendanceSummaryResponse attendance = attendanceService.getMyAttendance(studentEmail);

        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
        Font headFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font bodyFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 50, 40);
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            Paragraph title = new Paragraph("Academic Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            doc.add(title);
            Paragraph date = new Paragraph("Generated on " + LocalDate.now(), bodyFont);
            date.setAlignment(Element.ALIGN_CENTER);
            date.setSpacingAfter(20);
            doc.add(date);

            doc.add(new Paragraph("Student Name : " + student.getStudentName(), bodyFont));
            doc.add(new Paragraph("Roll Number  : " + student.getRollNumber(), bodyFont));
            doc.add(new Paragraph("Department   : " + student.getDepartment(), bodyFont));
            doc.add(new Paragraph("Semester     : " + student.getSemester(), bodyFont));

            Paragraph marksTitle = new Paragraph("Subject-wise Marks", headFont);
            marksTitle.setSpacingBefore(20);
            marksTitle.setSpacingAfter(8);
            doc.add(marksTitle);

            PdfPTable table = new PdfPTable(new float[]{3.2f, 1.6f, 2f, 1.5f, 1.5f});
            table.setWidthPercentage(100);
            for (String h : new String[]{"Subject", "Code", "Exam", "Obtained", "Max"}) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setPadding(6);
                table.addCell(cell);
            }
            for (MarksResponse m : marks.subjectWiseMarks()) {
                table.addCell(cell(m.subjectName(), bodyFont));
                table.addCell(cell(m.subjectCode(), bodyFont));
                table.addCell(cell(m.examType(), bodyFont));
                table.addCell(cell(String.valueOf(m.marksObtained()), bodyFont));
                table.addCell(cell(String.valueOf(m.maxMarks()), bodyFont));
            }
            doc.add(table);

            Paragraph summary = new Paragraph();
            summary.setSpacingBefore(20);
            summary.add(new Phrase("Total Marks : " + marks.totalMarksObtained() + " / " + marks.totalMaxMarks() + "\n", headFont));
            summary.add(new Phrase("Percentage : " + marks.percentage() + " %\n", headFont));
            summary.add(new Phrase("Attendance : " + attendance.attendancePercentage() + " % ("
                    + attendance.presentCount() + " of " + attendance.totalClasses() + " classes)", headFont));
            doc.add(summary);
        } catch (DocumentException e) {
            throw new IllegalStateException("Could not generate PDF report", e);
        } finally {
            if (doc.isOpen()) {
                doc.close();
            }
        }
        return out.toByteArray();
    }

    private PdfPCell cell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setPadding(5);
        return c;
    }
}
