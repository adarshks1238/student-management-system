package com.org.sms.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.org.sms.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUserEmail(String email);

    boolean existsByEmployeeId(String employeeId);
}
