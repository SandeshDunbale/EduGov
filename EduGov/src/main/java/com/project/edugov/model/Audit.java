package com.project.edugov.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "audit")
@Data
public class Audit {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;
 
    @NotBlank(message = "Audit scope is required")
    @Size(min = 5, max = 255, message = "Scope must be between 5 and 255 characters")
    private String scope;

    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "Findings cannot exceed 2000 characters")
    private String findings;

    @NotBlank(message = "Audit status is required")
    private String status; // Ideally an Enum like 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED'

    @NotNull(message = "Audit date is required")
    @PastOrPresent(message = "Audit date cannot be in the future")
    private LocalDate date;
 
    @NotNull(message = "Assigning an officer to the audit is mandatory")
    @ManyToOne
    @JoinColumn(name = "officer_id", nullable = false)
    private User officer;
}