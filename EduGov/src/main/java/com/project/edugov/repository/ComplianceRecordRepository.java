package com.project.edugov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.ComplianceRecord;
import com.project.edugov.model.ComplianceRecordStatus;
import com.project.edugov.model.User;

@Repository
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {
    
    // Find records by entity (e.g., all compliance checks for a specific Program)
    List<ComplianceRecord> findByEntityIdAndEntityType(Long entityId, String entityType);
    
    // Find records by status (e.g., all FAILED checks)
    List<ComplianceRecord> findByResult(ComplianceRecordStatus result);
    
    // Find records created by a specific officer
    List<ComplianceRecord> findByOfficer(User officer);
}
