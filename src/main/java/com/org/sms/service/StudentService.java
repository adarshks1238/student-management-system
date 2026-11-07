package com.org.sms.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.sms.dto.StudentDTO;
import com.org.sms.entity.Student;
import com.org.sms.exception.ResourceNotFoundException;
import com.org.sms.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /** Looks up the student that belongs to the logged-in email (identity comes from the JWT, never from the client). */
    public Student getStudentByEmail(String email) {
        return studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for " + email));
    }

    @Transactional(readOnly = true)
    public StudentDTO getProfile(String email) {
        return toDTO(getStudentByEmail(email));
    }

    public static StudentDTO toDTO(Student s) {
        return new StudentDTO(s.getId(), s.getStudentName(), s.getRollNumber(), s.getDepartment(),
                s.getSemester(), s.getUser().getEmail());
    }
}
