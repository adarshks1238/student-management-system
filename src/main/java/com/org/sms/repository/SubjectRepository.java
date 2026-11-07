package com.org.sms.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.org.sms.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectCode(String subjectCode);

    boolean existsBySubjectCode(String subjectCode);
}
