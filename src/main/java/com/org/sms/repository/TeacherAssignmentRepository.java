package com.org.sms.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.org.sms.entity.TeacherAssignment;

public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, Long> {

    List<TeacherAssignment> findByTeacherId(Long teacherId);

    boolean existsByTeacherIdAndSubjectIdAndSemester(Long teacherId, Long subjectId, Integer semester);

    boolean existsBySubjectId(Long subjectId);

    void deleteByTeacherId(Long teacherId);
}
