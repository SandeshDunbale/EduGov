package com.project.edugov.repository;

import com.project.edugov.model.Audit;
import com.project.edugov.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

    // Find all audits by status (e.g., PENDING, APPROVED, REJECTED)
    List<Audit> findByStatus(String status);

    // Find audits assigned to a specific Government Auditor
    List<Audit> findByOfficer(User officer);
    
    // Find audits by scope containing a specific keyword
    List<Audit> findByScopeContainingIgnoreCase(String keyword);
}