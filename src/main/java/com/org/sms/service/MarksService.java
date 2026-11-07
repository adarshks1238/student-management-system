package com.org.sms.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.sms.dto.MarksRequest;
import com.org.sms.dto.MarksResponse;
import com.org.sms.dto.MarksSummaryResponse;
import com.org.sms.entity.Marks;
import com.org.sms.entity.Student;
import com.org.sms.entity.Subject;
import com.org.sms.entity.Teacher;
import com.org.sms.exception.BadRequestException;
import com.org.sms.exception.ResourceNotFoundException;
import com.org.sms.exception.UnauthorizedAccessException;
import com.org.sms.repository.MarksRepository;
import com.org.sms.repository.StudentRepository;
import com.org.sms.repository.SubjectRepository;

@Service
public class MarksService {

    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherService teacherService;
    private final StudentService studentService;

    public MarksService(MarksRepository marksRepository, StudentRepository studentRepository,
                        SubjectRepository subjectRepository, TeacherService teacherService,
                        StudentService studentService) {
        this.marksRepository = marksRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    @Transactional
    public MarksResponse addMarks(String teacherEmail, MarksRequest req) {
        Teacher teacher = teacherService.getTeacherByEmail(teacherEmail);
        Student student = findStudent(req.studentId());
        Subject subject = findSubject(req.subjectId());
        teacherService.verifyAssignment(teacher, student, subject);
        validateMarks(req);

        if (marksRepository.existsByStudentIdAndSubjectIdAndExamType(student.getId(), subject.getId(), req.examType().trim())) {
            throw new BadRequestException("Marks already exist for this student, subject and exam type. Use update instead.");
        }
        Marks marks = new Marks();
        marks.setStudent(student);
        marks.setSubject(subject);
        marks.setTeacher(teacher);
        apply(marks, req);
        return toResponse(marksRepository.save(marks));
    }

    @Transactional
    public MarksResponse updateMarks(String teacherEmail, Long marksId, MarksRequest req) {
        Teacher teacher = teacherService.getTeacherByEmail(teacherEmail);
        Marks marks = marksRepository.findById(marksId)
                .orElseThrow(() -> new ResourceNotFoundException("Marks record not found: " + marksId));
        // Re-check assignment against the stored record (not only the request body)
        teacherService.verifyAssignment(teacher, marks.getStudent(), marks.getSubject());
        if (!marks.getStudent().getId().equals(req.studentId()) || !marks.getSubject().getId().equals(req.subjectId())) {
            throw new BadRequestException("studentId/subjectId cannot be changed on an existing marks record");
        }
        validateMarks(req);
        apply(marks, req);
        marks.setTeacher(teacher);
        return toResponse(marksRepository.save(marks));
    }

    /** Teacher view: marks entered by this teacher, optionally filtered. */
    @Transactional(readOnly = true)
    public List<MarksResponse> getMarksForTeacher(String teacherEmail, Long studentId, Long subjectId) {
        Teacher teacher = teacherService.getTeacherByEmail(teacherEmail);
        return marksRepository.findByTeacherIdWithDetails(teacher.getId()).stream()
                .filter(m -> studentId == null || m.getStudent().getId().equals(studentId))
                .filter(m -> subjectId == null || m.getSubject().getId().equals(subjectId))
                .map(this::toResponse).toList();
    }

    /** Student view: always scoped to the logged-in student. */
    @Transactional(readOnly = true)
    public MarksSummaryResponse getMyMarks(String studentEmail) {
        Student student = studentService.getStudentByEmail(studentEmail);
        List<MarksResponse> list = marksRepository.findByStudentIdWithDetails(student.getId())
                .stream().map(this::toResponse).toList();
        double obtained = list.stream().mapToDouble(MarksResponse::marksObtained).sum();
        double max = list.stream().mapToDouble(MarksResponse::maxMarks).sum();
        double pct = max == 0 ? 0 : round2(obtained * 100.0 / max);
        return new MarksSummaryResponse(list, round2(obtained), round2(max), pct);
    }

    // ---- helpers ----
    private void validateMarks(MarksRequest req) {
        if (req.marksObtained() > req.maxMarks()) {
            throw new BadRequestException("marksObtained cannot be greater than maxMarks");
        }
    }

    private void apply(Marks m, MarksRequest req) {
        m.setMarksObtained(req.marksObtained());
        m.setMaxMarks(req.maxMarks());
        m.setExamType(req.examType().trim());
        m.setExamDate(req.examDate());
    }

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }

    private Subject findSubject(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + id));
    }

    private MarksResponse toResponse(Marks m) {
        return new MarksResponse(m.getId(), m.getStudent().getId(), m.getStudent().getStudentName(),
                m.getStudent().getRollNumber(), m.getSubject().getId(), m.getSubject().getSubjectName(),
                m.getSubject().getSubjectCode(), m.getMarksObtained(), m.getMaxMarks(),
                m.getExamType(), m.getExamDate());
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
