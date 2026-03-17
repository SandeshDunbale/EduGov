package com.project.edugov.dto;

import com.project.edugov.model.GrantApplicationStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GrantApplicationDTO {
    private Long applicationID;
    private Long projectId;
    private String projectTitle;
    private FacultyMinimalDTO faculty; // Reusing the clean Faculty DTO
    private BigDecimal requestedAmount;
    private GrantApplicationStatus status;
    private LocalDate submittedDate;
}
