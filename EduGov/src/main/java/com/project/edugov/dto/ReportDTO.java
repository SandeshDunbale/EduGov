package com.project.edugov.dto;

import java.time.LocalDate;

public class ReportDTO {
    
    private Long reportId;
    private String scope;
    private String metrics;
    private LocalDate generatedDate;

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getMetrics() { return metrics; }
    public void setMetrics(String metrics) { this.metrics = metrics; }

    public LocalDate getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDate generatedDate) { this.generatedDate = generatedDate; }
}