package com.adarsh.sms.repository;

import com.adarsh.sms.entity.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUserEmail(String email);

    /** Students whose semester matches any semester the teacher is assigned to teach. */
    @Query("select s from Student s where s.semester in "
            + "(select ta.semester from TeacherAssignment ta where ta.teacher.id = :teacherId) "
            + "order by s.rollNumber")
    List<Student> findAssignedStudents(@Param("teacherId") Long teacherId);
}
