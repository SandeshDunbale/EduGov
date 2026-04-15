package com.project.edugov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	boolean existsByTitleIgnoreCase(String title);

	List<Course> findByFacultyFacultyId(Long facultyId);

	List<Course> findByProgramProgramID(Long programId);

	boolean existsByTitleIgnoreCaseAndProgram_ProgramID(String title, Long programId);

}