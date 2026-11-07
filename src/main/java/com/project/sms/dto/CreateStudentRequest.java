package com.project.sms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStudentRequest(
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
        @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String password,
        @NotBlank(message = "Student name is required") String studentName,
        @NotBlank(message = "Roll number is required") String rollNumber,
        @NotBlank(message = "Department is required") String department,
        @NotNull(message = "Semester is required") @Min(value = 1, message = "Semester must be 1-12") @Max(value = 12, message = "Semester must be 1-12") Integer semester) {
}
