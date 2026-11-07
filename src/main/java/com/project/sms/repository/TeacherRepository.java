package com.project.sms.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.sms.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUserEmail(String email);

    boolean existsByEmployeeId(String employeeId);
}
