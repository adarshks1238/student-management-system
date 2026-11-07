package com.adarsh.sms.dto;

import com.adarsh.sms.entity.AttendanceStatus;
import java.time.LocalDate;

public record AttendanceResponse(Long id, Long studentId, String studentName, String rollNumber,
                                 Long subjectId, String subjectName, LocalDate attendanceDate,
                                 AttendanceStatus status) {
}
