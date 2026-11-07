package com.project.sms.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sms.dto.AttendanceSummaryResponse;
import com.project.sms.dto.MarksSummaryResponse;
import com.project.sms.dto.StudentDTO;
import com.project.sms.service.AttendanceService;
import com.project.sms.service.MarksService;
import com.project.sms.service.StudentService;

/** Every endpoint resolves the student from the JWT principal, so a student can never request someone else's data. */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final MarksService marksService;
    private final AttendanceService attendanceService;

    public StudentController(StudentService studentService, MarksService marksService,
                             AttendanceService attendanceService) {
        this.studentService = studentService;
        this.marksService = marksService;
        this.attendanceService = attendanceService;
    }

    @GetMapping("/profile")
    public StudentDTO profile(Authentication auth) {
        return studentService.getProfile(auth.getName());
    }

    @GetMapping("/my-marks")
    public MarksSummaryResponse myMarks(Authentication auth) {
        return marksService.getMyMarks(auth.getName());
    }

    @GetMapping("/my-attendance")
    public AttendanceSummaryResponse myAttendance(Authentication auth) {
        return attendanceService.getMyAttendance(auth.getName());
    }
}
