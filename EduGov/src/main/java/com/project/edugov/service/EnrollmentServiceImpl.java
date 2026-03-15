package com.project.edugov.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.edugov.model.*;
import com.project.edugov.repository.*;
import com.project.edugov.exception.ResourceNotFoundException;
import com.project.edugov.exception.EnrollmentException;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepo;
    
    @Autowired
    private StudentRepository studentRepo;
    
    @Autowired
    private CourseRepository courseRepo;
    
    @Autowired
    private UserRepository userRepo;

    @Override
    public Enrollment applyForEnrollment(Long studentId, Long courseId) {
        // Step 1: Validate foreign keys (Throw 404 if missing)
        Student student = studentRepo.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        
        Course course = courseRepo.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        // Step 2: Check for existing enrollment (Throw 400 if duplicate)
        if (enrollmentRepo.existsByStudent_StudentIdAndCourse_CourseID(studentId, courseId)) {
            throw new EnrollmentException("Application Failed: Student is already enrolled in " + course.getTitle());
        }

        // Step 3: Map and Save
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(Status.PENDING);
        return enrollmentRepo.save(enrollment);
    }

    @Override
    public Enrollment updateEnrollmentStatus(Long enrollmentId, Status status, Long adminId) {
        // Verify existence of record and admin
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Enrollment record not found for ID: " + enrollmentId));
        
        User admin = userRepo.findById(adminId)
            .orElseThrow(() -> new ResourceNotFoundException("Admin verification failed for ID: " + adminId));

        enrollment.setStatus(status);
        enrollment.setApprovedByAdmin(admin);
        return enrollmentRepo.save(enrollment);
    }

    @Override
    public List<Enrollment> getPendingEnrollments() {
        List<Enrollment> list = enrollmentRepo.findByStatus(Status.PENDING);
        if(list.isEmpty()) {
            throw new ResourceNotFoundException("No pending enrollment requests currently exist.");
        }
        return list;
    }

    @Override
    public List<Long> getStudentIdsByStatus(Status status) {
        // If status filter returns nothing, it simply returns an empty list
        return enrollmentRepo.findStudentIdsByStatus(status);
    }
}