package com.project.edugov.service;

import com.project.edugov.model.*;
import com.project.edugov.repository.*;
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

    @Override
    public Course addCourse(Course course, Long programId, Long facultyId, Long adminId) {
        Program program = programRepo.findById(programId).orElseThrow(() -> new RuntimeException("Program not found"));
        Faculty faculty = facultyRepo.findById(facultyId).orElseThrow(() -> new RuntimeException("Faculty not found"));
        
        course.setProgram(program);
        course.setFaculty(faculty);
        return courseRepo.save(course);
    }

    @Override
    public List<Course> getCoursesByProgram(Long programId) {
        return courseRepo.findByProgram_ProgramID(programId);
    }

    @Override
    public List<Course> getCoursesByFaculty(Long facultyId) {
        return courseRepo.findByFaculty_FacultyId(facultyId);
    }
}