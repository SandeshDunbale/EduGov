package com.project.edugov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.Faculty;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
   
}

