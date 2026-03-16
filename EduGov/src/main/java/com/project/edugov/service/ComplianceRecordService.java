
package com.project.edugov.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.project.edugov.model.ComplianceRecord;
import com.project.edugov.repository.ComplianceRecordRepository;

@Service
public class ComplianceRecordService {

    // Use constructor injection (cleaner for testing and immutability)
    private final ComplianceRecordRepository repository;

    public ComplianceRecordService(ComplianceRecordRepository repository) {
        this.repository = repository;
    }

    // Save a new compliance record
    public ComplianceRecord saveComplianceRecord(ComplianceRecord record) {
        return repository.save(record);
    }

    // Get compliance record by id
    public ComplianceRecord getComplianceRecordById(Long id) {
        return repository.findById(id).orElse(null);
    }

    // Get all compliance records
    public List<ComplianceRecord> getAllComplianceRecords() {
        return repository.findAll();
    }

    // Get records by entity type (PROGRAM, PROJECT, GRANT, ENROLLMENT)
    public List<ComplianceRecord> getByEntityType(String entityType) {
        return repository.findByEntityType(entityType);
    }

    // Get records by entity id
    public List<ComplianceRecord> getByEntityId(Long entityId) {
        return repository.findByEntityId(entityId);
    }

    // Get records by entity type and entity id
    public List<ComplianceRecord> getByEntityTypeAndEntityId(String entityType, Long entityId) {
        return repository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    // Get records by result (COMPLIANT / NON_COMPLIANT)
    public List<ComplianceRecord> getByResult(String result) {
        return repository.findByResult(result);
    }

    // Get records by entity type and result
    public List<ComplianceRecord> getByEntityTypeAndResult(String entityType, String result) {
        return repository.findByEntityTypeAndResult(entityType, result);
    }

    // Get records by entity id and result
    public List<ComplianceRecord> getByEntityIdAndResult(Long entityId, String result) {
        return repository.findByEntityIdAndResult(entityId, result);
    }

    // Delete compliance record
    public void deleteComplianceRecord(Long id) {
        repository.deleteById(id);
    }
}
