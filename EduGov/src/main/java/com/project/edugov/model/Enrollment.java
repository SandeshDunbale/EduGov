package com.project.edugov.model;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
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
import lombok.Data;
 
@Entity
@Table(name = "enrollments")
@Data
public class Enrollment {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentID;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id",referencedColumnName="studentId" ,nullable = false)
    private Student student;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName="courseID",nullable = false)
    private Course course;
 
    @CreationTimestamp
    @Column(name = "enrollment_date", updatable = false)
    private LocalDateTime date;
 
    // Default status is PENDING for all new requests
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING; 
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_admin_id",referencedColumnName="userId")
    private User user;
}