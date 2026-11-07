package com.adarsh.sms.controller;

import com.adarsh.sms.dto.CreateStudentRequest;
import com.adarsh.sms.dto.StudentDTO;
import com.adarsh.sms.dto.SubjectDTO;
import com.adarsh.sms.dto.TeacherDTO;
import com.adarsh.sms.dto.UpdateStudentRequest;
import com.adarsh.sms.service.TeacherService;
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

    @PostMapping("/students")
    public ResponseEntity<StudentDTO> createStudent(Authentication auth, @Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.createStudent(auth.getName(), request));
    }

    @PutMapping("/students/{id}")
    public StudentDTO updateStudent(Authentication auth, @PathVariable Long id,
                                    @Valid @RequestBody UpdateStudentRequest request) {
        return teacherService.updateStudent(auth.getName(), id, request);
    }

    @GetMapping("/subjects")
    public List<SubjectDTO> subjects(Authentication auth) {
        return teacherService.getAssignedSubjects(auth.getName());
    }
}
