package com.project.edugov.service;
 
import com.project.edugov.model.Report;
import com.project.edugov.model.ReportScope;
import com.project.edugov.repository.ReportRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
 
@Service
@RequiredArgsConstructor
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;
 
    // 🔥 MAIN METHOD
    public Report generateReport(ReportScope scope) {
 
        Map<String, Object> metrics = new HashMap<>();//STORE dynamic map
 
        // 👉 PROGRAM REPORT
        if (scope == ReportScope.PROGRAM) {
            metrics.put("totalPrograms", 10);
            metrics.put("activePrograms", 7);
            metrics.put("inactivePrograms", 3);
        }
 
        // 👉 PROJECT REPORT
        else if (scope == ReportScope.PROJECT) {
            metrics.put("totalProjects", 20);
            metrics.put("draftProjects", 5);
            metrics.put("completedProjects", 10);
        }
 
        // 👉 GRANT REPORT
        else if (scope == ReportScope.GRANT) {
            metrics.put("projectName", "AI Research");
            metrics.put("projectAmount", 50000);
            metrics.put("totalGrantAmount", 200000);
        }
 
        Report report = new Report();
        report.setScope(scope);
        report.setMetrics(metrics.toString()); // JSON stored as string
        report.setGeneratedDate(LocalDateTime.now());
 
        return reportRepository.save(report);
    }
}