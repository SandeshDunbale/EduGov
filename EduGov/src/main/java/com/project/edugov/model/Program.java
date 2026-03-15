package com.project.edugov.model;


import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
 
@Entity
@Table(name = "programs")
@Data
public class Program {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long programID;
 
    @NotBlank(message = "Program title cannot be empty")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
 
    @Column(columnDefinition = "TEXT")
    private String description;

    @FutureOrPresent(message = "Start date cannot be in the past")
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
 
    @NotNull(message = "End date is required")
    private LocalDate endDate;
 
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE; 
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id",referencedColumnName = "userId", updatable = false)
    private User createdByAdmin;
}