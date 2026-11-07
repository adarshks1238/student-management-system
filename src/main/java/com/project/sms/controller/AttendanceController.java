package com.project.sms.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.sms.dto.AttendanceRequest;
import com.project.sms.dto.AttendanceResponse;
import com.project.sms.service.AttendanceService;

@RestController
@RequestMapping("/api/teachers/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<AttendanceResponse> mark(Authentication auth, @Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.markAttendance(auth.getName(), request));
    }

    @PutMapping("/{id}")
    public AttendanceResponse update(Authentication auth, @PathVariable Long id,
                                     @Valid @RequestBody AttendanceRequest request) {
        return attendanceService.updateAttendance(auth.getName(), id, request);
    }

    @GetMapping
    public List<AttendanceResponse> list(Authentication auth,
                                         @RequestParam(required = false) Long studentId,
                                         @RequestParam(required = false) Long subjectId) {
        return attendanceService.getAttendanceForTeacher(auth.getName(), studentId, subjectId);
    }
}
