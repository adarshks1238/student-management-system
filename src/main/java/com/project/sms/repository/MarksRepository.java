package com.project.sms.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.sms.entity.Marks;

public interface MarksRepository extends JpaRepository<Marks, Long> {

    @Query("select m from Marks m join fetch m.student join fetch m.subject sub "
            + "where m.student.id = :studentId order by sub.subjectName, m.examDate")
    List<Marks> findByStudentIdWithDetails(@Param("studentId") Long studentId);

    @Query("select m from Marks m join fetch m.student st join fetch m.subject "
            + "where m.teacher.id = :teacherId order by st.rollNumber, m.examDate")
    List<Marks> findByTeacherIdWithDetails(@Param("teacherId") Long teacherId);

    boolean existsByStudentIdAndSubjectIdAndExamType(Long studentId, Long subjectId, String examType);

    boolean existsByTeacherId(Long teacherId);

    boolean existsBySubjectId(Long subjectId);

    void deleteByStudentId(Long studentId);
}
