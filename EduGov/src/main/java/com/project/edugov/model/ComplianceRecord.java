package com.project.edugov.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name= "compliance_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplianceRecord {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long complianceId;
	
	private Long entityId;
	
	private String entityType;
	
	private String result;
	
	private LocalDate date;
	
	private String notes;

	@ManyToOne
	@JoinColumn(name = "officer_id", nullable = true)
	private User officer;

	public Long getComplianceId() {
		return complianceId;
	}

	public void setComplianceId(Long complianceId) {
		this.complianceId = complianceId;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public User getOfficer() {
		return officer;
	}

	public void setOfficer(User officer) {
		this.officer = officer;
	}

}
	
