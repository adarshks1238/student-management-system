package com.adarsh.sms.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTeacherRequest(
        @NotBlank(message = "Teacher name is required") String teacherName,
        @NotBlank(message = "Employee ID is required") String employeeId) {
}
