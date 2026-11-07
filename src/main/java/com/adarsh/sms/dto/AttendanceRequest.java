package com.adarsh.sms.dto;

import com.adarsh.sms.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AttendanceRequest(
        @NotNull(message = "studentId is required") Long studentId,
        @NotNull(message = "subjectId is required") Long subjectId,
        @NotNull(message = "attendanceDate is required") LocalDate attendanceDate,
        @NotNull(message = "status is required (PRESENT or ABSENT)") AttendanceStatus status) {
}
