package com.org.sms.dto;

public record StudentAdminDTO(Long id, String studentName, String rollNumber, String department,
                              Integer semester, String email, boolean enabled) {
}
