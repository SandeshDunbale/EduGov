package com.project.edugov.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.edugov.dto.CourseDTO;
import com.project.edugov.model.Course;
import com.project.edugov.service.CourseService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/courses")
@Slf4j
public class CourseController {

	@Autowired
	private CourseService courseService;

	// Admin create new course
	@PostMapping("/save")
	public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody Course course) {
		Long pId = course.getProgram().getProgramID();
		Long fId = course.getFaculty().getFacultyId();
		Long aId = course.getUser().getUserId();
		log.info("Request: Create course '{}' by Admin ID: {} for Program ID: {}", course.getTitle(), aId, pId);
		return new ResponseEntity<>(courseService.createCourse(course, pId, fId, aId), HttpStatus.CREATED);
	}

	// Find course by faculty Id
	@GetMapping("/faculty/{facultyId}")
	public ResponseEntity<List<CourseDTO>> getCoursesByFaculty(@PathVariable Long facultyId) {
		log.info("Request: Fetch courses for Faculty ID: {}", facultyId);
		List<CourseDTO> response = courseService.getCoursesByFacultyId(facultyId);
		return ResponseEntity.ok(response);
	}

	// Find courses by program Id
	@GetMapping("/program/{programId}")
	public ResponseEntity<List<CourseDTO>> getCoursesByProgram(@PathVariable Long programId) {
		log.info("Request: Fetch courses for Program ID: {}", programId);
		return ResponseEntity.ok(courseService.getCoursesByProgramId(programId));
	}

	// Find courses by course Id
	@GetMapping("/{courseId}")
	public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long courseId) {
		log.info("Request: Fetching course details for ID: {}", courseId);
		return ResponseEntity.ok(courseService.getCourseById(courseId));
	}

	// Update course details
	@PatchMapping("/update/{id}")
	public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody Course details) {
		log.info("Request: Update the Course with ID: {}", id);
		CourseDTO updatedCourse = courseService.updateCourse(id, details);
		return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
	}

	// Get all courses
	@GetMapping("/all")
	public ResponseEntity<List<CourseDTO>> getAllCourses() {
		log.info("Request: Fetch all courses");
		List<CourseDTO> response = courseService.getAllCourses();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}