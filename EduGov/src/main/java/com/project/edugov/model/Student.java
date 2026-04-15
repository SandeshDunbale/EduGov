package com.project.edugov.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "students")
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId; // Auto-generated 1, 2, 3...
     private String name;
    private LocalDate dob;
    private String gender;
    private String address;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id") 
    // Foreign Key to Users table
    private User user;
}