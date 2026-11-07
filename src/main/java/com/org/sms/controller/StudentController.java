package com.org.sms.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.sms.dto.AttendanceSummaryResponse;
import com.org.sms.dto.MarksSummaryResponse;
import com.org.sms.dto.StudentDTO;
import com.org.sms.service.AttendanceService;
import com.org.sms.service.MarksService;
import com.org.sms.service.StudentService;

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
