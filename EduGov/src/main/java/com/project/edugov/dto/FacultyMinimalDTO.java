package com.project.edugov.dto;


import lombok.Data;

@Data
public class FacultyMinimalDTO {
    private Long facultyId;
    private String facultyName; // We will map this from User.name
}