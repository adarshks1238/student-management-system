package com.adarsh.sms.controller;

import com.adarsh.sms.dto.StudentDTO;
import com.adarsh.sms.dto.SubjectDTO;
import com.adarsh.sms.dto.TeacherDTO;
import com.adarsh.sms.service.TeacherService;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/profile")
    public TeacherDTO profile(Authentication auth) {
        return teacherService.getProfile(auth.getName());
    }

    @GetMapping("/students")
    public List<StudentDTO> students(Authentication auth) {
        return teacherService.getAssignedStudents(auth.getName());
    }

    @GetMapping("/subjects")
    public List<SubjectDTO> subjects(Authentication auth) {
        return teacherService.getAssignedSubjects(auth.getName());
    }
}
