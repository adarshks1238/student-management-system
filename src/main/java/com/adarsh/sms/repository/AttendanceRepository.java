package com.adarsh.sms.repository;

import com.adarsh.sms.entity.Attendance;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("select a from Attendance a join fetch a.student join fetch a.subject "
            + "where a.student.id = :studentId order by a.attendanceDate desc")
    List<Attendance> findByStudentIdWithDetails(@Param("studentId") Long studentId);

    @Query("select a from Attendance a join fetch a.student st join fetch a.subject "
            + "where a.teacher.id = :teacherId order by a.attendanceDate desc, st.rollNumber")
    List<Attendance> findByTeacherIdWithDetails(@Param("teacherId") Long teacherId);

    boolean existsByStudentIdAndSubjectIdAndAttendanceDate(Long studentId, Long subjectId, LocalDate date);
}
