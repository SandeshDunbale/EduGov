package com.project.edugov.repository;

import com.project.edugov.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Requirement: Get courses assigned to a specific Faculty ID
    List<Course> findByFaculty_FacultyId(Long facultyId);
    
    // Requirement: Get all courses under a specific Program
    List<Course> findByProgram_ProgramID(Long programId);
}