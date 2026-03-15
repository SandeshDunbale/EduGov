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
import lombok.Builder;
import lombok.Data;
 
@Data
@Builder
@Entity
@Table(name = "resource",
       indexes = {
           @Index(name = "idx_resource_program", columnList = "ProgramID"),
           @Index(name = "idx_resource_status", columnList = "status")
       })
public class Resource {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ResourceID")
    private Long resourceId;
 
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ProgramID",referencedColumnName="programID", nullable = false,
            foreignKey = @ForeignKey(name = "fk_resource_program"))
    private Program program;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 16, nullable = false)
    private ResourceType type; // FUNDS/LAB/EQUIPMENT
 
    @Column(name = "quantity")
    private Integer quantity;   // optional (for countable items)
 
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private ResourceStatus status; // AVAILABLE/ALLOCATED/MAINTENANCE/RETIRED
 
}