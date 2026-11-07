package com.adarsh.sms.repository;

import com.adarsh.sms.entity.TeacherAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, Long> {

    List<TeacherAssignment> findByTeacherId(Long teacherId);

    boolean existsByTeacherIdAndSubjectIdAndSemester(Long teacherId, Long subjectId, Integer semester);

    boolean existsBySubjectId(Long subjectId);

    void deleteByTeacherId(Long teacherId);
}
