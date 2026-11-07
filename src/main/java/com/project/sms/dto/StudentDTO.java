package com.project.sms.dto;

public record StudentDTO(Long id, String studentName, String rollNumber, String department,
                         Integer semester, String email) {
}
