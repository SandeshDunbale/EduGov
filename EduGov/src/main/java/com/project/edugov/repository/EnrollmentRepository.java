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

    List<Enrollment> findByStatus(Status status);

    @Query("SELECT DISTINCT e.student.studentId FROM Enrollment e WHERE e.status = :status")
    List<Long> findStudentIdsByStatus(@Param("status") Status status);
    
    List<Enrollment> findByStudent_StudentId(Long studentId);

    // FIXED: Changed CourseId to CourseID to match your Model variable
    boolean existsByStudent_StudentIdAndCourse_CourseID(Long studentId, Long courseID);
}