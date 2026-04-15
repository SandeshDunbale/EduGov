package com.project.edugov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.Enrollment;
import com.project.edugov.model.Status;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	boolean existsByStudent_StudentIdAndCourse_CourseID(Long studentId, Long courseId);

	List<Enrollment> findByStatus(Status status);

}