package com.project.edugov.dto;

import com.project.edugov.model.ComplianceRecordStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ComplianceRecordDTO {
    private Long complianceId;
    private Long entityId;
    private String entityType;
    private ComplianceRecordStatus result;
    private LocalDate date;
    private String notes;
    private String officerName;
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
	public ComplianceRecordStatus getResult() {
		return result;
	}
	public void setResult(ComplianceRecordStatus result) {
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
	public String getOfficerName() {
		return officerName;
	}
	public void setOfficerName(String officerName) {
		this.officerName = officerName;
	}
}