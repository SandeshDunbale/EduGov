package com.project.edugov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}