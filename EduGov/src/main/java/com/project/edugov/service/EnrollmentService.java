package com.project.edugov.service;

import com.project.edugov.model.Enrollment;
import com.project.edugov.model.Status;
import java.util.List;

public interface EnrollmentService {
    Enrollment applyForEnrollment(Long studentId, Long courseId);
    Enrollment updateEnrollmentStatus(Long enrollmentId, Status status, Long adminId);
    List<Enrollment> getPendingEnrollments();
    List<Long> getStudentIdsByStatus(Status status); // Active/Rejected tracking
}