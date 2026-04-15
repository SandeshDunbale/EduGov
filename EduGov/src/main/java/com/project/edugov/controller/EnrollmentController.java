package com.project.edugov.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.edugov.dto.EnrollmentResponseDTO;
import com.project.edugov.model.Status;
import com.project.edugov.service.EnrollmentService;
import com.project.edugov.service.AuditServiceImpl;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/enrollments")
@Slf4j
public class EnrollmentController {

	@Autowired
	private EnrollmentService enrollmentService;

	@Autowired
	private AuditServiceImpl auditService;

	// Student enroll in course
	@PostMapping("/apply")
	public ResponseEntity<EnrollmentResponseDTO> apply(@RequestBody Map<String, Long> request) {
		Long studentId = request.get("studentId");
		Long courseId = request.get("courseId");
		
		auditService.logAction("APPLY_COURSE", "COURSE_ID_" + courseId);
		
		log.info("Request: Student ID {} applying for Course ID {}", studentId, courseId);
		EnrollmentResponseDTO response = enrollmentService.applyForCourse(studentId, courseId);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Find Enrollments by status
	@GetMapping("/status/{status}")
	public ResponseEntity<List<EnrollmentResponseDTO>> getByStatus(@PathVariable Status status) {
		log.info("Request: Search enrollments by status: {}", status);
		List<EnrollmentResponseDTO> response = enrollmentService.getEnrollmentsByStatus(status);
		return ResponseEntity.ok(response);
	}

	// Update the enrollment status
	@PutMapping("/update/{eId}/admin/{aId}/status/{status}")
	public ResponseEntity<EnrollmentResponseDTO> updateEnrollment(@PathVariable Long eId, @PathVariable Long aId,
			@PathVariable Status status) {
		
		auditService.logAction("UPDATE_ENROLLMENT_STATUS_" + status.name(), "ENROLLMENT_ID_" + eId);
		
		log.info("Request: Updating Enrollment {} to {} by Admin {}", eId, status, aId);
		EnrollmentResponseDTO response = enrollmentService.updateEnrollmentStatus(eId, aId, status);
		return ResponseEntity.ok(response);
	}

	// Get all enrolment
	@GetMapping("/all")
	public ResponseEntity<List<EnrollmentResponseDTO>> getAll() {
		log.info("Request: Fetching all enrollments");
		List<EnrollmentResponseDTO> list = enrollmentService.getAllEnrollments();
		return ResponseEntity.ok(list);
	}
}