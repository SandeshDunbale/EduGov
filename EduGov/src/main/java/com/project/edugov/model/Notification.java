package com.project.edugov.model;

import java.time.LocalDateTime;

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

@Table(name = "notifications") 

@Data 

public class Notification { 

 

    @Id 

    @GeneratedValue(strategy = GenerationType.IDENTITY) 

    private Long notificationID; 

 

    // RELATIONSHIP: Many notifications belong to one User 

    @ManyToOne(fetch = FetchType.LAZY) 

    @JoinColumn(name = "user_id", nullable = false) 

    private User recipient; 

 

    // Link to the specific Program, Project, or Grant ID 

    @Column(name = "entity_id") 

    private Long entityID; 

 

    @Column(nullable = false) 

    private String message; 

 

    @Enumerated(EnumType.STRING) 

    private NotificationCategory category; 

 

    private String status; 

 

    private LocalDateTime createdDate;
}