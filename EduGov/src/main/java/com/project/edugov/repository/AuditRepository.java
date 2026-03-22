package com.project.edugov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.edugov.model.Audit;

public interface AuditRepository extends JpaRepository<Audit, Long> {

}