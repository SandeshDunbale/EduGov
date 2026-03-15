package com.project.edugov.dto;

import com.project.edugov.model.Status;
import lombok.Data;

@Data
public class CourseDTO {
    private Long courseID;
    private String title;
    private String description;
    private Long programId; 
    private Long facultyId;
    private Long adminId;
    private Status status;
}