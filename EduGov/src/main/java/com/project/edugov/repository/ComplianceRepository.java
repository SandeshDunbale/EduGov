package com.project.edugov.repository;

import com.project.edugov.model.ComplianceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplianceRepository extends JpaRepository<ComplianceRecord, Long> {
    
    // Prevents duplicate flagging of the same project/program
    boolean existsByEntityIdAndEntityType(Long entityId, String entityType);
}