package com.project.sms.dto;

import jakarta.validation.constraints.NotNull;

public record AssignmentRequest(
        @NotNull(message = "teacherId is required") Long teacherId,
        @NotNull(message = "subjectId is required") Long subjectId,
        @NotNull(message = "semester is required") Integer semester) {
}
