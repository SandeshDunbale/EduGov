package com.project.edugov.dto;

import java.time.LocalDateTime;
import com.project.edugov.model.Status;
import lombok.Data;

@Data
public class EnrollmentResponseDTO {
    private Long enrollmentID;
    private String studentName; // Flattened from Student entity
    private String courseTitle;  // Flattened from Course entity
    private LocalDateTime enrollmentDate;
    private Status status;
    private String approvedByAdminName;
}