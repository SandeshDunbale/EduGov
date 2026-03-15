

package com.project.edugov.repository;

import java.util.Optional;
import com.project.edugov.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.edugov.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find student using User's primary key
    Optional<Student> findByUser_UserId(Long userId);

    // Check if student already exists for a specific user
    boolean existsByUser_UserId(Long userId);

    // Filter students by status (PENDING, ACTIVE, etc.)
    Page<Student> findAllByStatus(Status status, Pageable pageable);
}