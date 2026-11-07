package com.adarsh.sms.service;

import com.adarsh.sms.dto.AttendanceRequest;
import com.adarsh.sms.dto.AttendanceResponse;
import com.adarsh.sms.dto.AttendanceSummaryResponse;
import com.adarsh.sms.entity.Attendance;
import com.adarsh.sms.entity.AttendanceStatus;
import com.adarsh.sms.entity.Student;
import com.adarsh.sms.entity.Subject;
import com.adarsh.sms.entity.Teacher;
import com.adarsh.sms.exception.BadRequestException;
import com.adarsh.sms.exception.ResourceNotFoundException;
import com.adarsh.sms.repository.AttendanceRepository;
import com.adarsh.sms.repository.StudentRepository;
import com.adarsh.sms.repository.SubjectRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherService teacherService;
    private final StudentService studentService;

    public AttendanceService(AttendanceRepository attendanceRepository, StudentRepository studentRepository,
                             SubjectRepository subjectRepository, TeacherService teacherService,
                             StudentService studentService) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    @Transactional
    public AttendanceResponse markAttendance(String teacherEmail, AttendanceRequest req) {
        Teacher teacher = teacherService.getTeacherByEmail(teacherEmail);
        Student student = studentRepository.findById(req.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + req.studentId()));
        Subject subject = subjectRepository.findById(req.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + req.subjectId()));
        teacherService.verifyAssignment(teacher, student, subject);

        if (attendanceRepository.existsByStudentIdAndSubjectIdAndAttendanceDate(
                student.getId(), subject.getId(), req.attendanceDate())) {
            throw new BadRequestException("Attendance already marked for this student, subject and date. Use update instead.");
        }
        Attendance a = new Attendance();
        a.setStudent(student);
        a.setSubject(subject);
        a.setTeacher(teacher);
        a.setAttendanceDate(req.attendanceDate());
        a.setStatus(req.status());
        return toResponse(attendanceRepository.save(a));
    }

    @Transactional
    public AttendanceResponse updateAttendance(String teacherEmail, Long id, AttendanceRequest req) {
        Teacher teacher = teacherService.getTeacherByEmail(teacherEmail);
        Attendance a = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found: " + id));
        teacherService.verifyAssignment(teacher, a.getStudent(), a.getSubject());
        if (!a.getStudent().getId().equals(req.studentId()) || !a.getSubject().getId().equals(req.subjectId())) {
            throw new BadRequestException("studentId/subjectId cannot be changed on an existing attendance record");
        }
        a.setAttendanceDate(req.attendanceDate());
        a.setStatus(req.status());
        a.setTeacher(teacher);
        return toResponse(attendanceRepository.save(a));
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceForTeacher(String teacherEmail, Long studentId, Long subjectId) {
        Teacher teacher = teacherService.getTeacherByEmail(teacherEmail);
        return attendanceRepository.findByTeacherIdWithDetails(teacher.getId()).stream()
                .filter(a -> studentId == null || a.getStudent().getId().equals(studentId))
                .filter(a -> subjectId == null || a.getSubject().getId().equals(subjectId))
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getMyAttendance(String studentEmail) {
        Student student = studentService.getStudentByEmail(studentEmail);
        List<AttendanceResponse> list = attendanceRepository.findByStudentIdWithDetails(student.getId())
                .stream().map(this::toResponse).toList();
        long total = list.size();
        long present = list.stream().filter(r -> r.status() == AttendanceStatus.PRESENT).count();
        double pct = total == 0 ? 0 : Math.round(present * 10000.0 / total) / 100.0;
        return new AttendanceSummaryResponse(list, total, present, total - present, pct);
    }

    private AttendanceResponse toResponse(Attendance a) {
        return new AttendanceResponse(a.getId(), a.getStudent().getId(), a.getStudent().getStudentName(),
                a.getStudent().getRollNumber(), a.getSubject().getId(), a.getSubject().getSubjectName(),
                a.getAttendanceDate(), a.getStatus());
    }
}
