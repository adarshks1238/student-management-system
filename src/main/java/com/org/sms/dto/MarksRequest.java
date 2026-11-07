package com.org.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

public record MarksRequest(
        @NotNull(message = "studentId is required") Long studentId,
        @NotNull(message = "subjectId is required") Long subjectId,
        @NotNull(message = "marksObtained is required") @PositiveOrZero(message = "marksObtained cannot be negative") Double marksObtained,
        @NotNull(message = "maxMarks is required") @Positive(message = "maxMarks must be greater than 0") Double maxMarks,
        @NotBlank(message = "examType is required") String examType,
        @NotNull(message = "examDate is required") LocalDate examDate) {
}
