package com.project.edugov.dto;

import java.time.LocalDate;
import com.project.edugov.model.Status;
import lombok.Data;

@Data
public class ProgramDTO {
    private Long programID; // Included for viewing/responses
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private Long adminId; // Used only during creation
}