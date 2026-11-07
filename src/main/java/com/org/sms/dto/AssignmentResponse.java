package com.adarsh.sms.dto;

public record AssignmentResponse(Long id, Long teacherId, String teacherName, Long subjectId,
                                 String subjectName, String subjectCode, Integer semester) {
}
