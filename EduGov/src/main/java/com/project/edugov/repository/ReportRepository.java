package com.project.edugov.repository;
 
import com.project.edugov.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface ReportRepository extends JpaRepository<Report, Long> {
}