package com.project.edugov.repository;

import com.project.edugov.model.Enrollment;
import com.project.edugov.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Requirement: Fetch all "PENDING" requests for the Admin
    List<Enrollment> findByStatus(Status status);

    // Requirement: Get unique Student IDs based on status (ACTIVE/REJECTED)
    @Query("SELECT DISTINCT e.student.studentId FROM Enrollment e WHERE e.status = :status")
    List<Long> findStudentIdsByStatus(@Param("status") Status status);
    
    // Requirement: Get a specific student's enrollment history
    List<Enrollment> findByStudent_StudentId(Long studentId);
}