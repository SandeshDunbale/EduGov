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

@Entity
@Table(
   name = "students",
   uniqueConstraints = @UniqueConstraint(name = "uq_student_user", columnNames = "user_id")
)
public class Student {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long studentId;

   // 1:1 with users via unique FK
   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "user_id",referencedColumnName = "userId", nullable = false,
           foreignKey = @ForeignKey(name = "fk_student_user"))
   private User user;

   private LocalDate dob;

   @Column(length = 20)
   private String gender;

   @Column(length = 500)
   private String address;

   @Enumerated(EnumType.STRING)
   private Status status = Status.PENDING; 
   
}