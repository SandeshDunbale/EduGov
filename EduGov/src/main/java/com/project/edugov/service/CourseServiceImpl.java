package com.project.edugov.service;

import com.project.edugov.model.*;
import com.project.edugov.repository.*;
import com.project.edugov.exception.ResourceNotFoundException; // Added
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepo;
    
    @Autowired
    private ProgramRepository programRepo;
    
    @Autowired
    private FacultyRepository facultyRepo;
    
    @Autowired
    private UserRepository userRepo; // Added to verify Admin ID

    @Override
    public Course addCourse(Course course, Long programId, Long facultyId, Long adminId) {
        // API: POST /api/courses
        
        // 1. Verify Admin exists
        userRepo.findById(adminId)
            .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + adminId));

        // 2. Verify Program exists
        Program program = programRepo.findById(programId)
            .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + programId));
        
        // 3. Verify Faculty exists
        Faculty faculty = facultyRepo.findById(facultyId)
            .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with ID: " + facultyId));
        
        course.setProgram(program);
        course.setFaculty(faculty);
        return courseRepo.save(course);
    }

    @Override
    public List<Course> getCoursesByProgram(Long programId) {
        // First check if program exists
        if (!programRepo.existsById(programId)) {
            throw new ResourceNotFoundException("Cannot fetch courses. Program ID " + programId + " not found");
        }
        return courseRepo.findByProgram_ProgramID(programId);
    }

    @Override
    public List<Course> getCoursesByFaculty(Long facultyId) {
        // API: GET /api/courses/faculty/{facultyId}
        List<Course> courses = courseRepo.findByFaculty_FacultyId(facultyId);
        
        // If faculty has no courses, throw exception so Postman shows a clear message
        if (courses.isEmpty()) {
            throw new ResourceNotFoundException("No courses assigned to Faculty ID: " + facultyId);
        }
        return courses;
    }
}