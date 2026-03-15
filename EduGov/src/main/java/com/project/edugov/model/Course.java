package com.project.edugov.model;
 
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
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
 
@Entity
@Table(name = "courses")
@Data
public class Course {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseID;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id",referencedColumnName="programID", nullable = false)
    private Program program;
 
    @NotBlank(message = "Course title is mandatory")
    private String title;
 
    @Column(columnDefinition = "TEXT")
    private String description;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id",referencedColumnName = "facultyId")
    private Faculty faculty;
 
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE; 
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id",referencedColumnName = "userId", updatable = false)
    private User user;
}