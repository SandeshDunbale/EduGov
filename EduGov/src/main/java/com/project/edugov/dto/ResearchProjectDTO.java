package com.project.edugov.dto;

import com.project.edugov.model.ProjectStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ResearchProjectDTO {
    private Long projectId;
    private String title;
    private String description;
    private FacultyMinimalDTO faculty; // Nested object
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
}
