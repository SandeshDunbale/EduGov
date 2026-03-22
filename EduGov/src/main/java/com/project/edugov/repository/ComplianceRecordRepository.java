package com.project.edugov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.project.edugov.model.ComplianceRecord;

public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {

    List<ComplianceRecord> findByEntityType(String entityType);

    List<ComplianceRecord> findByResult(String result);
}