package com.org.sms.service;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.sms.dto.CreateStudentRequest;
import com.org.sms.dto.StudentDTO;
import com.org.sms.dto.SubjectDTO;
import com.org.sms.dto.TeacherDTO;
import com.org.sms.dto.UpdateStudentRequest;
import com.org.sms.entity.Role;
import com.org.sms.entity.Student;
import com.org.sms.entity.Subject;
import com.org.sms.entity.Teacher;
import com.org.sms.entity.User;
import com.org.sms.exception.BadRequestException;
import com.org.sms.exception.ResourceNotFoundException;
import com.org.sms.exception.UnauthorizedAccessException;
import com.org.sms.repository.StudentRepository;
import com.org.sms.repository.TeacherAssignmentRepository;
import com.org.sms.repository.TeacherRepository;
import com.org.sms.repository.UserRepository;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherAssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(TeacherRepository teacherRepository,
                          TeacherAssignmentRepository assignmentRepository,
                          StudentRepository studentRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.assignmentRepository = assignmentRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Teacher getTeacherByEmail(String email) {
        return teacherRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found for " + email));
    }

    @Transactional(readOnly = true)
    public TeacherDTO getProfile(String email) {
        Teacher t = getTeacherByEmail(email);
        return new TeacherDTO(t.getId(), t.getTeacherName(), t.getEmployeeId(), t.getUser().getEmail());
    }

    @Transactional(readOnly = true)
    public List<SubjectDTO> getAssignedSubjects(String email) {
        Teacher t = getTeacherByEmail(email);
        return assignmentRepository.findByTeacherId(t.getId()).stream()
                .map(a -> new SubjectDTO(a.getSubject().getId(), a.getSubject().getSubjectName(),
                        a.getSubject().getSubjectCode(), a.getSemester()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> getAssignedStudents(String email) {
        Teacher t = getTeacherByEmail(email);
        return studentRepository.findAssignedStudents(t.getId()).stream()
                .map(StudentService::toDTO).toList();
    }

    /** Teacher creates a student account + profile (only for semesters the teacher is assigned to). */
    @Transactional
    public StudentDTO createStudent(String teacherEmail, CreateStudentRequest r) {
        Teacher teacher = getTeacherByEmail(teacherEmail);
        requireSemesterAssigned(teacher, r.semester());

        String roll = r.rollNumber().trim();
        if (studentRepository.existsByRollNumber(roll)) {
            throw new BadRequestException("Roll number already exists: " + roll);
        }
        String email = r.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already registered: " + email);
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(r.password()));
        user.setRole(Role.ROLE_STUDENT);
        user.setEnabled(true);
        user = userRepository.save(user);

        Student student = new Student();
        student.setUser(user);
        student.setStudentName(r.studentName().trim());
        student.setRollNumber(roll);
        student.setDepartment(r.department().trim());
        student.setSemester(r.semester());
        return StudentService.toDTO(studentRepository.save(student));
    }

    /** Teacher edits a student's details (student and target semester must both be in the teacher's scope). */
    @Transactional
    public StudentDTO updateStudent(String teacherEmail, Long studentId, UpdateStudentRequest r) {
        Teacher teacher = getTeacherByEmail(teacherEmail);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        requireSemesterAssigned(teacher, student.getSemester());
        requireSemesterAssigned(teacher, r.semester());

        String roll = r.rollNumber().trim();
        if (!student.getRollNumber().equals(roll) && studentRepository.existsByRollNumber(roll)) {
            throw new BadRequestException("Roll number already exists: " + roll);
        }
        student.setStudentName(r.studentName().trim());
        student.setRollNumber(roll);
        student.setDepartment(r.department().trim());
        student.setSemester(r.semester());
        return StudentService.toDTO(studentRepository.save(student));
    }

    /** Core business rule: a teacher may only act on (subject, student-semester) pairs assigned to them. */
    public void verifyAssignment(Teacher teacher, Student student, Subject subject) {
        boolean assigned = assignmentRepository.existsByTeacherIdAndSubjectIdAndSemester(
                teacher.getId(), subject.getId(), student.getSemester());
        if (!assigned) {
            throw new UnauthorizedAccessException("You are not assigned to teach "
                    + subject.getSubjectCode() + " to student " + student.getRollNumber());
        }
    }

    private void requireSemesterAssigned(Teacher teacher, Integer semester) {
        boolean ok = assignmentRepository.findByTeacherId(teacher.getId()).stream()
                .anyMatch(a -> a.getSemester().equals(semester));
        if (!ok) {
            throw new UnauthorizedAccessException("You are not assigned to teach any subject in semester " + semester);
        }
    }
}
