package com.project.edugov.model;
 
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "resource")
public class Resource {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ResourceID")
    private Long resourceId;
 
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ProgramID",referencedColumnName="programID", nullable = false)
    private Program program;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 16, nullable = false)
    private ResourceType type; // FUNDS/LAB/EQUIPMENT
 
    @Column(name = "quantity")
    private Integer quantity;   
 
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private ResourceStatus status; // AVAILABLE/ALLOCATED/MAINTENANCE/RETIRED
 
}