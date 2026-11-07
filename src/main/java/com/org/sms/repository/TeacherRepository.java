package com.adarsh.sms.repository;

import com.adarsh.sms.entity.Teacher;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUserEmail(String email);

    boolean existsByEmployeeId(String employeeId);
}
