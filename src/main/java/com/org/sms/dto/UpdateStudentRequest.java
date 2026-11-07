package com.org.sms.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateStudentRequest(
        @NotBlank(message = "Student name is required") String studentName,
        @NotBlank(message = "Roll number is required") String rollNumber,
        @NotBlank(message = "Department is required") String department,
        @NotNull(message = "Semester is required") @Min(value = 1, message = "Semester must be 1-12") @Max(value = 12, message = "Semester must be 1-12") Integer semester) {
}
