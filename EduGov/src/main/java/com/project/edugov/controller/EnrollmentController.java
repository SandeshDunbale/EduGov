package com.project.edugov.controller;

import com.project.edugov.dto.EnrollmentRequestDTO;
import com.project.edugov.model.Enrollment;
import com.project.edugov.model.Status;
import com.project.edugov.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/apply")
    public ResponseEntity<Enrollment> apply(@RequestBody EnrollmentRequestDTO request) {
        return ResponseEntity.ok(enrollmentService.applyForEnrollment(request.getStudentId(), request.getCourseId()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Enrollment> updateStatus(
            @PathVariable Long id, 
            @RequestParam Status status, 
            @RequestParam Long adminId) {
        // Decision Engine: APPROVED, REJECTED, etc.
        return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(id, status, adminId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Enrollment>> getPending() {
        return ResponseEntity.ok(enrollmentService.getPendingEnrollments());
    }

    @GetMapping("/students-by-status")
    public ResponseEntity<List<Long>> getStudentIds(@RequestParam Status status) {
        // Requirement: Filter student IDs by ACTIVE or REJECTED status
        return ResponseEntity.ok(enrollmentService.getStudentIdsByStatus(status));
    }
}