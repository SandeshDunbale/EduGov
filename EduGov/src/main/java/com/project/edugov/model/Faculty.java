package com.project.edugov.model;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(
    name = "faculty",
    uniqueConstraints = @UniqueConstraint(name = "uq_faculty_user", columnNames = "user_id")
)
@Data
public class Faculty {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facultyId;
 
   
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id",referencedColumnName = "userId", nullable = false,
            foreignKey = @ForeignKey(name = "fk_faculty_user"))
    private User user;
 
    private LocalDate dob;

    @Column(length = 150)
    private String department;
 
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING; 
}