package com.project.edugov.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.edugov.model.Audit;
import com.project.edugov.model.User;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
    List<Audit> findByStatus(String status);
    List<Audit> findByOfficer(User officer);
}
