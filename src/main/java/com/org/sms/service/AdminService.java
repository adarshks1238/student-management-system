package com.adarsh.sms.service;

import com.adarsh.sms.dto.AdminStatsResponse;
import com.adarsh.sms.dto.AssignmentRequest;
import com.adarsh.sms.dto.AssignmentResponse;
import com.adarsh.sms.dto.CreateTeacherRequest;
import com.adarsh.sms.dto.StudentAdminDTO;
import com.adarsh.sms.dto.SubjectDTO;
import com.adarsh.sms.dto.SubjectRequest;
import com.adarsh.sms.dto.TeacherAdminDTO;
import com.adarsh.sms.dto.UpdateTeacherRequest;
import com.adarsh.sms.entity.Role;
import com.adarsh.sms.entity.Student;
import com.adarsh.sms.entity.Subject;
import com.adarsh.sms.entity.Teacher;
import com.adarsh.sms.entity.TeacherAssignment;
import com.adarsh.sms.entity.User;
import com.adarsh.sms.exception.BadRequestException;
import com.adarsh.sms.exception.ResourceNotFoundException;
import com.adarsh.sms.repository.AttendanceRepository;
import com.adarsh.sms.repository.MarksRepository;
import com.adarsh.sms.repository.StudentRepository;
import com.adarsh.sms.repository.SubjectRepository;
import com.adarsh.sms.repository.TeacherAssignmentRepository;
import com.adarsh.sms.repository.TeacherRepository;
import com.adarsh.sms.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** All admin-only management operations: teachers, students, subjects and teacher assignments. */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherAssignmentRepository assignmentRepository;
    private final MarksRepository marksRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository, StudentRepository studentRepository,
                        TeacherRepository teacherRepository, SubjectRepository subjectRepository,
                        TeacherAssignmentRepository assignmentRepository, MarksRepository marksRepository,
                        AttendanceRepository attendanceRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.assignmentRepository = assignmentRepository;
        this.marksRepository = marksRepository;
        this.attendanceRepository = attendanceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------------- Stats ----------------
    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        return new AdminStatsResponse(teacherRepository.count(), studentRepository.count(),
                subjectRepository.count(), assignmentRepository.count());
    }

    // ---------------- Teachers ----------------
    @Transactional(readOnly = true)
    public List<TeacherAdminDTO> listTeachers() {
        return teacherRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public TeacherAdminDTO createTeacher(CreateTeacherRequest r) {
        String employeeId = r.employeeId().trim();
        if (teacherRepository.existsByEmployeeId(employeeId)) {
            throw new BadRequestException("Employee ID already exists: " + employeeId);
        }
        Teacher t = new Teacher();
        t.setUser(createUser(r.email(), r.password(), Role.ROLE_TEACHER));
        t.setTeacherName(r.teacherName().trim());
        t.setEmployeeId(employeeId);
        return toDTO(teacherRepository.save(t));
    }

    @Transactional
    public TeacherAdminDTO updateTeacher(Long id, UpdateTeacherRequest r) {
        Teacher t = findTeacher(id);
        String employeeId = r.employeeId().trim();
        if (!t.getEmployeeId().equals(employeeId) && teacherRepository.existsByEmployeeId(employeeId)) {
            throw new BadRequestException("Employee ID already exists: " + employeeId);
        }
        t.setTeacherName(r.teacherName().trim());
        t.setEmployeeId(employeeId);
        return toDTO(teacherRepository.save(t));
    }

    @Transactional
    public TeacherAdminDTO setTeacherEnabled(Long id, boolean enabled) {
        Teacher t = findTeacher(id);
        t.getUser().setEnabled(enabled);
        userRepository.save(t.getUser());
        return toDTO(t);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        Teacher t = findTeacher(id);
        if (marksRepository.existsByTeacherId(id) || attendanceRepository.existsByTeacherId(id)) {
            throw new BadRequestException("This teacher has marks/attendance records and cannot be deleted. Disable the account instead.");
        }
        User user = t.getUser();
        assignmentRepository.deleteByTeacherId(id);
        teacherRepository.delete(t);
        userRepository.delete(user);
    }

    // ---------------- Students ----------------
    @Transactional(readOnly = true)
    public List<StudentAdminDTO> listStudents() {
        return studentRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public StudentAdminDTO setStudentEnabled(Long id, boolean enabled) {
        Student s = findStudent(id);
        s.getUser().setEnabled(enabled);
        userRepository.save(s.getUser());
        return toDTO(s);
    }

    /** Deleting a student also deletes that student's marks and attendance. */
    @Transactional
    public void deleteStudent(Long id) {
        Student s = findStudent(id);
        User user = s.getUser();
        marksRepository.deleteByStudentId(id);
        attendanceRepository.deleteByStudentId(id);
        studentRepository.delete(s);
        userRepository.delete(user);
    }

    // ---------------- Subjects ----------------
    @Transactional(readOnly = true)
    public List<SubjectDTO> listSubjects() {
        return subjectRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public SubjectDTO createSubject(SubjectRequest r) {
        String code = r.subjectCode().trim().toUpperCase();
        if (subjectRepository.existsBySubjectCode(code)) {
            throw new BadRequestException("Subject code already exists: " + code);
        }
        Subject s = new Subject();
        s.setSubjectName(r.subjectName().trim());
        s.setSubjectCode(code);
        s.setSemester(r.semester());
        return toDTO(subjectRepository.save(s));
    }

    @Transactional
    public SubjectDTO updateSubject(Long id, SubjectRequest r) {
        Subject s = findSubject(id);
        String code = r.subjectCode().trim().toUpperCase();
        if (!s.getSubjectCode().equals(code) && subjectRepository.existsBySubjectCode(code)) {
            throw new BadRequestException("Subject code already exists: " + code);
        }
        s.setSubjectName(r.subjectName().trim());
        s.setSubjectCode(code);
        s.setSemester(r.semester());
        return toDTO(subjectRepository.save(s));
    }

    @Transactional
    public void deleteSubject(Long id) {
        Subject s = findSubject(id);
        if (assignmentRepository.existsBySubjectId(id) || marksRepository.existsBySubjectId(id)
                || attendanceRepository.existsBySubjectId(id)) {
            throw new BadRequestException("Subject is in use (assignments, marks or attendance) and cannot be deleted.");
        }
        subjectRepository.delete(s);
    }

    // ---------------- Teacher assignments ----------------
    @Transactional(readOnly = true)
    public List<AssignmentResponse> listAssignments() {
        return assignmentRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public AssignmentResponse createAssignment(AssignmentRequest r) {
        Teacher t = findTeacher(r.teacherId());
        Subject s = findSubject(r.subjectId());
        if (!s.getSemester().equals(r.semester())) {
            throw new BadRequestException("Assignment semester (" + r.semester()
                    + ") must match the subject's semester (" + s.getSemester() + ")");
        }
        if (assignmentRepository.existsByTeacherIdAndSubjectIdAndSemester(t.getId(), s.getId(), r.semester())) {
            throw new BadRequestException("This teacher is already assigned to this subject for that semester");
        }
        TeacherAssignment a = new TeacherAssignment();
        a.setTeacher(t);
        a.setSubject(s);
        a.setSemester(r.semester());
        return toDTO(assignmentRepository.save(a));
    }

    @Transactional
    public void deleteAssignment(Long id) {
        TeacherAssignment a = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + id));
        assignmentRepository.delete(a);
    }

    // ---------------- helpers ----------------
    private User createUser(String email, String rawPassword, Role role) {
        String normalized = email.trim().toLowerCase();
        if (userRepository.existsByEmail(normalized)) {
            throw new BadRequestException("Email already registered: " + normalized);
        }
        User u = new User();
        u.setEmail(normalized);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        u.setEnabled(true);
        return userRepository.save(u);
    }

    private Teacher findTeacher(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + id));
    }

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }

    private Subject findSubject(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + id));
    }

    private TeacherAdminDTO toDTO(Teacher t) {
        return new TeacherAdminDTO(t.getId(), t.getTeacherName(), t.getEmployeeId(),
                t.getUser().getEmail(), t.getUser().isEnabled());
    }

    private StudentAdminDTO toDTO(Student s) {
        return new StudentAdminDTO(s.getId(), s.getStudentName(), s.getRollNumber(), s.getDepartment(),
                s.getSemester(), s.getUser().getEmail(), s.getUser().isEnabled());
    }

    private SubjectDTO toDTO(Subject s) {
        return new SubjectDTO(s.getId(), s.getSubjectName(), s.getSubjectCode(), s.getSemester());
    }

    private AssignmentResponse toDTO(TeacherAssignment a) {
        return new AssignmentResponse(a.getId(), a.getTeacher().getId(), a.getTeacher().getTeacherName(),
                a.getSubject().getId(), a.getSubject().getSubjectName(), a.getSubject().getSubjectCode(),
                a.getSemester());
    }
}
