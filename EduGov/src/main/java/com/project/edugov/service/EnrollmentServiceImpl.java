package com.project.edugov.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.project.edugov.dto.EnrollmentResponseDTO;
import com.project.edugov.exception.APIException;
import com.project.edugov.exception.ResourceNotFoundException;
import com.project.edugov.model.Course;
import com.project.edugov.model.Enrollment;
import com.project.edugov.model.Status;
import com.project.edugov.model.Student;
import com.project.edugov.model.User;
import com.project.edugov.repository.CourseRepository;
import com.project.edugov.repository.EnrollmentRepository;
import com.project.edugov.repository.StudentRepository;
import com.project.edugov.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

	@Autowired
	private EnrollmentRepository enrollmentRepo;
	@Autowired
	private StudentRepository studentRepo;
	@Autowired
	private CourseRepository courseRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ModelMapper modelMapper;

	// Helper method
	private EnrollmentResponseDTO mapToCustomDto(Enrollment e) {
		EnrollmentResponseDTO dto = new EnrollmentResponseDTO();

		// Admin Info
		if (e.getApprovedByAdmin() != null) {
			dto.setApprovedByAdminId(e.getApprovedByAdmin().getUserId());
			dto.setApprovedByAdminName(e.getApprovedByAdmin().getName());
		}

		// Course & Faculty Info
		if (e.getCourse() != null) {
			dto.setCourseId(e.getCourse().getCourseID());
			dto.setCourseTitle(e.getCourse().getTitle());
			if (e.getCourse().getFaculty() != null) {
				dto.setFacultyid(e.getCourse().getFaculty().getFacultyId());
				dto.setFacultyname(e.getCourse().getFaculty().getUser().getName());
			}
		}

		// Student Info
		if (e.getStudent() != null && e.getStudent().getUser() != null) {
			dto.setStudentName(e.getStudent().getUser().getName());
			dto.setStudentEmail(e.getStudent().getUser().getEmail());
		}

		dto.setEnrollmentID(e.getEnrollmentID());
		dto.setEnrollmentDate(e.getDate());
		dto.setStatus(e.getStatus());

		return dto;
	}

	@Override
	public EnrollmentResponseDTO applyForCourse(Long sId, Long cId) {
		log.info("Process: Enrollment Hit for Student ID: {} -> Course ID: {}", sId, cId);

		Student s = studentRepo.findById(sId)
				.orElseThrow(() -> new ResourceNotFoundException("Student not found ID: " + sId));
		Course c = courseRepo.findById(cId)
				.orElseThrow(() -> new ResourceNotFoundException("Course not found ID: " + cId));

		// Can't enroll if course Inactive
		if (c.getStatus() != Status.ACTIVE) {
			log.warn("Enrollment blocked: Course is {}", c.getStatus());
			throw new APIException(HttpStatus.BAD_REQUEST, "Cannot enroll: Course is " + c.getStatus());
		}

		// Can't enroll if program Inactive
		if (c.getProgram() != null && c.getProgram().getStatus() != Status.ACTIVE) {
			log.warn("Enrollment blocked: Parent Program is {}", c.getProgram().getStatus());
			throw new APIException(HttpStatus.BAD_REQUEST, "Cannot enroll: Parent Program is INACTIVE.");
		}

		// No Duplicate Enrollment
		if (enrollmentRepo.existsByStudent_StudentIdAndCourse_CourseID(sId, cId)) {
			log.warn("Duplicate enrollment attempt for Student: {} in Course: {}", sId, cId);
			throw new APIException(HttpStatus.BAD_REQUEST, "Student is already enrolled in this course.");
		}

		Enrollment en = new Enrollment();
		en.setStudent(s);
		en.setCourse(c);
		en.setStatus(Status.PENDING);
		en.setDate(LocalDateTime.now());

		log.info("Saving Enrollment for Course: {}", c.getTitle());
		return mapToCustomDto(enrollmentRepo.save(en));
	}

	@Override
	public List<EnrollmentResponseDTO> getEnrollmentsByStatus(Status status) {
		log.info("Request: Fetching all enrollments with status: {}", status);

		List<Enrollment> enrollments = enrollmentRepo.findByStatus(status);

		if (enrollments.isEmpty()) {
			throw new ResourceNotFoundException("No enrollments found with status: " + status);
		}

		return enrollments.stream().map(this::mapToCustomDto).collect(Collectors.toList());
	}

	@Override
	public EnrollmentResponseDTO updateEnrollmentStatus(Long enrollmentId, Long adminId, Status newStatus) {
		log.info("Process: Admin {} updating Enrollment {} to {}", adminId, enrollmentId, newStatus);

		// Check enrollment
		Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Enrollment not found ID: " + enrollmentId));

		// Check admin
		User admin = userRepo.findById(adminId)
				.orElseThrow(() -> new ResourceNotFoundException("Admin not found ID: " + adminId));

		if (!admin.getRole().name().equalsIgnoreCase("UNIV_ADMIN")) {
			throw new APIException(HttpStatus.FORBIDDEN, "Access Denied: Only Admins can update status.");
		}

		// Admin Approve/Reject
		enrollment.setStatus(newStatus);
		enrollment.setApprovedByAdmin(admin);

		log.info("Status successfully changed to {} for Enrollment ID {}", newStatus, enrollmentId);

		Enrollment updatedEnrollment = enrollmentRepo.save(enrollment);
		return mapToCustomDto(updatedEnrollment);
	}

	@Override
	public List<EnrollmentResponseDTO> getAllEnrollments() {
		log.info("Process: Fetching all enrollments from database");

		List<Enrollment> allEnrollments = enrollmentRepo.findAll();

		return allEnrollments.stream().map(this::mapToCustomDto).collect(Collectors.toList());
	}
}