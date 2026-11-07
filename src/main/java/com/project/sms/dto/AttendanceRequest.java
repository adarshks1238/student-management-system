package com.project.sms.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

import com.project.sms.entity.AttendanceStatus;

public record AttendanceRequest(
        @NotNull(message = "studentId is required") Long studentId,
        @NotNull(message = "subjectId is required") Long subjectId,
        @NotNull(message = "attendanceDate is required") LocalDate attendanceDate,
        @NotNull(message = "status is required (PRESENT or ABSENT)") AttendanceStatus status) {
}
