package com.project.edugov.controller;

import com.project.edugov.model.Course;
import com.project.edugov.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<Course> addCourse(
            @RequestBody Course course, 
            @RequestParam Long programId, 
            @RequestParam Long facultyId, 
            @RequestParam Long adminId) {
        return ResponseEntity.ok(courseService.addCourse(course, programId, facultyId, adminId));
    }

    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<Course>> getCoursesByFaculty(@PathVariable Long facultyId) {
        // Requirement: Faculty can see their specific workload
        return ResponseEntity.ok(courseService.getCoursesByFaculty(facultyId));
    }
}