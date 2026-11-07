package com.project.sms.dto;

import java.time.LocalDate;

import com.project.sms.entity.AttendanceStatus;

public record AttendanceResponse(Long id, Long studentId, String studentName, String rollNumber,
                                 Long subjectId, String subjectName, LocalDate attendanceDate,
                                 AttendanceStatus status) {
}
