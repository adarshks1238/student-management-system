package com.org.sms.config;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.org.sms.entity.Attendance;
import com.org.sms.entity.AttendanceStatus;
import com.org.sms.entity.Marks;
import com.org.sms.entity.Role;
import com.org.sms.entity.Student;
import com.org.sms.entity.Subject;
import com.org.sms.entity.Teacher;
import com.org.sms.entity.TeacherAssignment;
import com.org.sms.entity.User;
import com.org.sms.repository.AttendanceRepository;
import com.org.sms.repository.MarksRepository;
import com.org.sms.repository.StudentRepository;
import com.org.sms.repository.SubjectRepository;
import com.org.sms.repository.TeacherAssignmentRepository;
import com.org.sms.repository.TeacherRepository;
import com.org.sms.repository.UserRepository;

/**
 * Inserts demo data on first start so the app is usable immediately.
 * Disable with app.seed.enabled=false.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherAssignmentRepository assignmentRepository;
    private final MarksRepository marksRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, StudentRepository studentRepository,
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

    @Override
    @Transactional
    public void run(String... args) {
        boolean freshDatabase = userRepository.count() == 0;

        // Admin account is created if it does not exist yet (works on existing databases too)
        if (!userRepository.existsByEmail("admin@sms.com")) {
            user("admin@sms.com", "Admin@123", Role.ROLE_ADMIN);
            log.info("Admin created. Admin: admin@sms.com / Admin@123");
        }
        if (!freshDatabase) {
            return;
        }
        // Subjects
        Subject ds = subject("Data Structures", "CS301", 3);
        Subject dbms = subject("Database Management Systems", "CS302", 3);
        Subject os = subject("Operating Systems", "CS303", 3);
        subject("Computer Networks", "CS501", 5);

        // Teachers
        Teacher t1 = teacher("teacher@sms.com", "Teacher@123", "Prof. Ramesh Kumar", "EMP001");
        Teacher t2 = teacher("teacher2@sms.com", "Teacher@123", "Prof. Anita Sharma", "EMP002");

        // Assignments (teacher1: DS + DBMS, teacher2: OS) for semester 3
        assign(t1, ds, 3);
        assign(t1, dbms, 3);
        assign(t2, os, 3);

        // Students (student3 is semester 5 -> not assigned to any seeded teacher)
        Student s1 = student("student1@sms.com", "Aarav Patel", "CSE2023001", "Computer Science", 3);
        Student s2 = student("student2@sms.com", "Diya Singh", "CSE2023002", "Computer Science", 3);
        student("student3@sms.com", "Rohan Mehta", "CSE2021003", "Computer Science", 5);

        // Sample marks
        marks(s1, ds, t1, 42, 50, "MIDTERM");
        marks(s1, dbms, t1, 38, 50, "MIDTERM");
        marks(s2, ds, t1, 45, 50, "MIDTERM");

        // Sample attendance
        LocalDate today = LocalDate.now();
        attendance(s1, ds, t1, today.minusDays(3), AttendanceStatus.PRESENT);
        attendance(s1, ds, t1, today.minusDays(2), AttendanceStatus.ABSENT);
        attendance(s1, dbms, t1, today.minusDays(1), AttendanceStatus.PRESENT);
        attendance(s2, ds, t1, today.minusDays(3), AttendanceStatus.PRESENT);

        log.info("Demo data created. Teacher: teacher@sms.com / Teacher@123 | Student: student1@sms.com / Student@123");
    }

    private User user(String email, String rawPassword, Role role) {
        User u = new User();
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        u.setEnabled(true);
        return userRepository.save(u);
    }

    private Subject subject(String name, String code, int semester) {
        Subject s = new Subject();
        s.setSubjectName(name);
        s.setSubjectCode(code);
        s.setSemester(semester);
        return subjectRepository.save(s);
    }

    private Teacher teacher(String email, String pw, String name, String empId) {
        Teacher t = new Teacher();
        t.setUser(user(email, pw, Role.ROLE_TEACHER));
        t.setTeacherName(name);
        t.setEmployeeId(empId);
        return teacherRepository.save(t);
    }

    private Student student(String email, String name, String roll, String dept, int sem) {
        Student s = new Student();
        s.setUser(user(email, "Student@123", Role.ROLE_STUDENT));
        s.setStudentName(name);
        s.setRollNumber(roll);
        s.setDepartment(dept);
        s.setSemester(sem);
        return studentRepository.save(s);
    }

    private void assign(Teacher t, Subject s, int semester) {
        TeacherAssignment a = new TeacherAssignment();
        a.setTeacher(t);
        a.setSubject(s);
        a.setSemester(semester);
        assignmentRepository.save(a);
    }

    private void marks(Student st, Subject sub, Teacher t, double obtained, double max, String exam) {
        Marks m = new Marks();
        m.setStudent(st);
        m.setSubject(sub);
        m.setTeacher(t);
        m.setMarksObtained(obtained);
        m.setMaxMarks(max);
        m.setExamType(exam);
        m.setExamDate(LocalDate.now().minusDays(10));
        marksRepository.save(m);
    }

    private void attendance(Student st, Subject sub, Teacher t, LocalDate date, AttendanceStatus status) {
        Attendance a = new Attendance();
        a.setStudent(st);
        a.setSubject(sub);
        a.setTeacher(t);
        a.setAttendanceDate(date);
        a.setStatus(status);
        attendanceRepository.save(a);
    }
}
