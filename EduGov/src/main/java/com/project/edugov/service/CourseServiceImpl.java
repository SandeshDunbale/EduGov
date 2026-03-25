package com.project.edugov.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.project.edugov.dto.CourseDTO;
import com.project.edugov.exception.APIException;
import com.project.edugov.exception.ResourceNotFoundException;
import com.project.edugov.model.Course;
import com.project.edugov.model.Faculty;
import com.project.edugov.model.Program;
import com.project.edugov.model.Status;
import com.project.edugov.model.User;
import com.project.edugov.repository.CourseRepository;
import com.project.edugov.repository.FacultyRepository;
import com.project.edugov.repository.ProgramRepository;
import com.project.edugov.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

	@Autowired
	private CourseRepository courseRepo;
	@Autowired
	private ProgramRepository programRepo;
	@Autowired
	private FacultyRepository facultyRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ModelMapper modelMapper;

	// Helper method
	private CourseDTO mapToCustomDto(Course c, User admin) {
		CourseDTO dto = modelMapper.map(c, CourseDTO.class);

		// Map Admin
		if (admin != null) {
			dto.setAdminId(admin.getUserId());
			dto.setAdminName(admin.getName());
		}

		// Set Faculty details
		if (c.getFaculty() != null) {
			dto.setFacultyId(c.getFaculty().getFacultyId());
			dto.setFacultyName(c.getFaculty().getUser().getName());
			dto.setFacultyEmail(c.getFaculty().getUser().getEmail());
		}

		// Set Program details
		if (c.getProgram() != null) {
			dto.setProgramID(c.getProgram().getProgramID());
			dto.setProgramTitle(c.getProgram().getTitle());
			dto.setProgramStatus(c.getProgram().getStatus().toString());
		}
		return dto;
	}

	@Override
	public CourseDTO createCourse(Course course, Long pId, Long fId, Long aId) {
		// check user
		User user = userRepo.findById(aId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + aId));

		if (!user.getRole().name().equalsIgnoreCase("UNIV_ADMIN")) {
			throw new APIException(HttpStatus.FORBIDDEN, "Access Denied: Only University Admins can create courses.");
		}

		// check program active
		Program program = programRepo.findById(pId)
				.orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + pId));

		// Can't create if program is Inactive
		if (program.getStatus() == null || !program.getStatus().name().equalsIgnoreCase("ACTIVE")) {
			throw new APIException(HttpStatus.BAD_REQUEST,
					"Cannot create course: The Program '" + program.getTitle() + "' is currently INACTIVE.");
		}

		// Check faculty
		Faculty faculty = facultyRepo.findById(fId)
				.orElseThrow(() -> new ResourceNotFoundException("Faculty not found with ID: " + fId));

		// Check tittle
		if (courseRepo.existsByTitleIgnoreCase(course.getTitle())) {
			throw new APIException(HttpStatus.BAD_REQUEST, "Course title already exists.");
		}

		course.setProgram(program);
		course.setFaculty(faculty);
		course.setUser(user);

		log.info("Saving new course '{}' under Program: {}", course.getTitle(), program.getTitle());
		return mapToCustomDto(courseRepo.save(course), user);
	}

	@Override
	public List<CourseDTO> getCoursesByFacultyId(Long facultyId) {
		log.info("Fetching all courses for Faculty ID: {}", facultyId);

		// Check faculty
		facultyRepo.findById(facultyId)
				.orElseThrow(() -> new ResourceNotFoundException("Faculty not found with ID: " + facultyId));

		// Get courses
		List<Course> courses = courseRepo.findByFacultyFacultyId(facultyId);

		if (courses.isEmpty()) {
			throw new APIException(HttpStatus.NOT_FOUND,
					"No courses are currently assigned to Faculty ID: " + facultyId);
		}

		// 4. Map to DTO
		return courses.stream().map(c -> mapToCustomDto(c, c.getUser())).collect(Collectors.toList());
	}

	@Override
	public List<CourseDTO> getCoursesByProgramId(Long programId) {
		log.info("Fetching all courses for Program ID: {}", programId);

		// Check Program exists
		programRepo.findById(programId)
				.orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + programId));

		// Get courses
		List<Course> courses = courseRepo.findByProgramProgramID(programId);

		if (courses.isEmpty()) {
			throw new APIException(HttpStatus.NOT_FOUND,
					"No courses are currently assigned to Program ID: " + programId);
		}

		// Map to DTO
		return courses.stream().map(c -> mapToCustomDto(c, c.getUser())).collect(Collectors.toList());
	}

	@Override
	public CourseDTO getCourseById(Long courseId) {
		log.info("Fetching course details for Course ID: {}", courseId);

		// Get course
		Course course = courseRepo.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

		// 3. Map to DTO
		return mapToCustomDto(course, course.getUser());
	}

	@Override
	public CourseDTO updateCourse(Long id, Course details) {
		log.info("Request: Updating Course ID: {}", id);

		Course existing = courseRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));

		// Title Validation
		if (details.getTitle() != null) {
			String newTitle = details.getTitle().trim();
			if (!newTitle.equalsIgnoreCase(existing.getTitle())) {
				if (courseRepo.existsByTitleIgnoreCaseAndProgram_ProgramID(newTitle,
						existing.getProgram().getProgramID())) {
					throw new APIException(HttpStatus.BAD_REQUEST, "Title already used in this program.");
				}
			}
		}

		if (details.getStatus() != null && !details.getStatus().equals(Status.ACTIVE)) {
			if (details.getStatus().equals(existing.getStatus())) {
				throw new APIException(HttpStatus.BAD_REQUEST,
						"Update failed: Status is already " + existing.getStatus());
			}
		}

		// Description Redundancy Check
		if (details.getDescription() != null && details.getDescription().equals(existing.getDescription())) {
			throw new APIException(HttpStatus.BAD_REQUEST, "Update failed: Description is already the same.");
		}

		// Partial Update
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		modelMapper.map(details, existing);

		log.info("Saving updates for Course ID: {}", id);
		Course saved = courseRepo.save(existing);

		return mapToCustomDto(saved, saved.getUser());
	}

	@Override
	public List<CourseDTO> getAllCourses() {
		log.info("Fetching all courses from the database");

		// Retrieve all courses
		List<Course> courses = courseRepo.findAll();

		if (courses.isEmpty()) {
			throw new ResourceNotFoundException("No courses found in the system.");
		}

		// 3. Map to DTO
		return courses.stream().map(c -> mapToCustomDto(c, c.getUser())) // Safe mapping for Admin/User
				.collect(Collectors.toList());
	}
}