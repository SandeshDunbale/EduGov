package com.project.edugov.service;

import java.time.LocalDate;
import java.util.List;

import com.project.edugov.dto.ReportDTO;
import com.project.edugov.model.ReportScope;

public interface ReportService {
    ReportDTO generateReportByScope(ReportScope scope);
    ReportDTO generateReport(ReportDTO dto);
    List<ReportDTO> getAllReports();
    List<ReportDTO> getReportsByScope(String scope);
    List<ReportDTO> getReportsByDateRange(LocalDate start, LocalDate end);
}