package com.project.sms.dto;

import java.time.LocalDate;

public record MarksResponse(Long id, Long studentId, String studentName, String rollNumber,
                            Long subjectId, String subjectName, String subjectCode,
                            Double marksObtained, Double maxMarks, String examType, LocalDate examDate) {
}
