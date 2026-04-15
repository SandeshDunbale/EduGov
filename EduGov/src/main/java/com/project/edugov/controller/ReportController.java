package com.project.edugov.controller;

import com.project.edugov.dto.ReportDTO;
import com.project.edugov.model.ReportScope;
import com.project.edugov.service.ReportService;
import com.project.edugov.service.AuditServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    private AuditServiceImpl auditService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate") 
    public ReportDTO generateReport(@RequestParam ReportScope scope) {
        auditService.logAction("GENERATE_REPORT", "SCOPE_" + scope.name());
        return reportService.generateReportByScope(scope);
    }

    @GetMapping
    public List<ReportDTO> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/scope/{scope}")
    public List<ReportDTO> getReportsByScope(@PathVariable String scope) {
        return reportService.getReportsByScope(scope);
    }

    @GetMapping("/date-range")
    public List<ReportDTO> getReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        
        return reportService.getReportsByDateRange(start, end);
    }
}