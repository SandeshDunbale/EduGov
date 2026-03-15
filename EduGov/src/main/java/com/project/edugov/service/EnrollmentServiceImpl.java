package com.project.edugov.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.edugov.model.Course;
import com.project.edugov.model.Enrollment;
import com.project.edugov.model.Status;
import com.project.edugov.model.Student;
import com.project.edugov.model.User;
import com.project.edugov.repository.CourseRepository;
import com.project.edugov.repository.EnrollmentRepository;
import com.project.edugov.repository.StudentRepository;
import com.project.edugov.repository.UserRepository;

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
        Student student = studentRepo.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepo.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(Status.PENDING); // Force initial state
        return enrollmentRepo.save(enrollment);
    }

    @Override
    public Enrollment updateEnrollmentStatus(Long enrollmentId, Status status, Long adminId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId).orElseThrow(() -> new RuntimeException("Enrollment not found"));
        User admin = userRepo.findById(adminId).orElseThrow(() -> new RuntimeException("Admin not found"));

        enrollment.setStatus(status); // APPROVED or REJECTED
        enrollment.setApprovedByAdmin(admin); // Decision maker
        return enrollmentRepo.save(enrollment);
    }

    @Override
    public List<Enrollment> getPendingEnrollments() {
        return enrollmentRepo.findByStatus(Status.PENDING);
    }

    @Override
    public List<Long> getStudentIdsByStatus(Status status) {
        return enrollmentRepo.findStudentIdsByStatus(status);
    }
}