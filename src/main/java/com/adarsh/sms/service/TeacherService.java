package com.adarsh.sms.service;

import com.adarsh.sms.dto.StudentDTO;
import com.adarsh.sms.dto.SubjectDTO;
import com.adarsh.sms.dto.TeacherDTO;
import com.adarsh.sms.entity.Student;
import com.adarsh.sms.entity.Subject;
import com.adarsh.sms.entity.Teacher;
import com.adarsh.sms.exception.ResourceNotFoundException;
import com.adarsh.sms.exception.UnauthorizedAccessException;
import com.adarsh.sms.repository.StudentRepository;
import com.adarsh.sms.repository.TeacherAssignmentRepository;
import com.adarsh.sms.repository.TeacherRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherAssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;

    public TeacherService(TeacherRepository teacherRepository,
                          TeacherAssignmentRepository assignmentRepository,
                          StudentRepository studentRepository) {
        this.teacherRepository = teacherRepository;
        this.assignmentRepository = assignmentRepository;
        this.studentRepository = studentRepository;
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

    /** Core business rule: a teacher may only act on (subject, student-semester) pairs assigned to them. */
    public void verifyAssignment(Teacher teacher, Student student, Subject subject) {
        boolean assigned = assignmentRepository.existsByTeacherIdAndSubjectIdAndSemester(
                teacher.getId(), subject.getId(), student.getSemester());
        if (!assigned) {
            throw new UnauthorizedAccessException("You are not assigned to teach "
                    + subject.getSubjectCode() + " to student " + student.getRollNumber());
        }
    }
}
