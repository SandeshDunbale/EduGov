package com.project.edugov.dto;

import com.project.edugov.model.GrantStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GrantResponseDTO {
    private Long grantId;
    private String projectTitle;
    private BigDecimal amount;
    private LocalDate date;
    private GrantStatus status;
   // private String approvedByName; // Mapped from User.name of the approver
    private String approvedByRole;
}
