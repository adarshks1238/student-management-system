package com.org.sms.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.sms.dto.AdminStatsResponse;
import com.org.sms.dto.AssignmentRequest;
import com.org.sms.dto.AssignmentResponse;
import com.org.sms.dto.CreateTeacherRequest;
import com.org.sms.dto.StudentAdminDTO;
import com.org.sms.dto.SubjectDTO;
import com.org.sms.dto.SubjectRequest;
import com.org.sms.dto.TeacherAdminDTO;
import com.org.sms.dto.UpdateTeacherRequest;
import com.org.sms.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public AdminStatsResponse stats() {
        return adminService.getStats();
    }

    // ---- Teachers ----
    @GetMapping("/teachers")
    public List<TeacherAdminDTO> teachers() {
        return adminService.listTeachers();
    }

    @PostMapping("/teachers")
    public ResponseEntity<TeacherAdminDTO> createTeacher(@Valid @RequestBody CreateTeacherRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createTeacher(r));
    }

    @PutMapping("/teachers/{id}")
    public TeacherAdminDTO updateTeacher(@PathVariable Long id, @Valid @RequestBody UpdateTeacherRequest r) {
        return adminService.updateTeacher(id, r);
    }

    @PutMapping("/teachers/{id}/enabled")
    public TeacherAdminDTO teacherEnabled(@PathVariable Long id, @RequestParam boolean value) {
        return adminService.setTeacherEnabled(id, value);
    }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        adminService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Students ----
    @GetMapping("/students")
    public List<StudentAdminDTO> students() {
        return adminService.listStudents();
    }

    @PutMapping("/students/{id}/enabled")
    public StudentAdminDTO studentEnabled(@PathVariable Long id, @RequestParam boolean value) {
        return adminService.setStudentEnabled(id, value);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        adminService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Subjects ----
    @GetMapping("/subjects")
    public List<SubjectDTO> subjects() {
        return adminService.listSubjects();
    }

    @PostMapping("/subjects")
    public ResponseEntity<SubjectDTO> createSubject(@Valid @RequestBody SubjectRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createSubject(r));
    }

    @PutMapping("/subjects/{id}")
    public SubjectDTO updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectRequest r) {
        return adminService.updateSubject(id, r);
    }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        adminService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Teacher assignments ----
    @GetMapping("/assignments")
    public List<AssignmentResponse> assignments() {
        return adminService.listAssignments();
    }

    @PostMapping("/assignments")
    public ResponseEntity<AssignmentResponse> createAssignment(@Valid @RequestBody AssignmentRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAssignment(r));
    }

    @DeleteMapping("/assignments/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        adminService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
