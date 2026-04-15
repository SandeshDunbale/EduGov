package com.project.edugov.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.project.edugov.model.Report;
import com.project.edugov.model.ReportScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByScope(ReportScope scope);

    List<Report> findByGeneratedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}