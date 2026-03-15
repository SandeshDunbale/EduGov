package com.project.edugov.model;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "academic_documents",
    indexes = {
        @Index(name = "idx_acadoc_user", columnList = "user_id"),
        @Index(name = "idx_acadoc_status", columnList = "verification_status")
    }
)
public class AcademicDocument {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;
 
    // 1:N from user
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "userId",nullable = false,
            foreignKey = @ForeignKey(name = "fk_doc_user"))
    private User user;
 
    // e.g. "TRANSCRIPT", "CERTIFICATE", "ID_PROOF", "OTHER"
    @Column(nullable = false, length = 50)
    private String docType;
 
    @Column(nullable = false, length = 1024)
    private String fileUrl;
 
    // "PENDING" | "APPROVED" | "DECLINED"
    @Enumerated(EnumType.STRING)
    private Status verificationStatus = Status.PENDING; 
 
    @Column(nullable = false)
    private Instant uploadedDate = Instant.now();
 
    // who verified (University Admin user)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_user_id",
            foreignKey = @ForeignKey(name = "fk_doc_verified_by"))
    private User verifiedBy;
 
    private Instant verifiedAt;
 
    @Column(length = 1000)
    private String notes;
}