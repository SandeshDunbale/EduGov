package com.project.edugov.service;

import com.project.edugov.model.Course;
import java.util.List;

public interface CourseService {
    Course addCourse(Course course, Long programId, Long facultyId, Long adminId);
    List<Course> getCoursesByProgram(Long programId);
    List<Course> getCoursesByFaculty(Long facultyId); // New requirement
}