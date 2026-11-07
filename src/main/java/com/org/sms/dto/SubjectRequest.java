package com.org.sms.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubjectRequest(
        @NotBlank(message = "Subject name is required") String subjectName,
        @NotBlank(message = "Subject code is required") String subjectCode,
        @NotNull(message = "Semester is required") @Min(value = 1, message = "Semester must be 1-12") @Max(value = 12, message = "Semester must be 1-12") Integer semester) {
}
