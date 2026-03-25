package com.project.edugov.model;
 
import java.time.LocalDateTime;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
 
@Entity
@Table(name = "reports")
@Data
public class Report {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportScope scope;
 
    // We use columnDefinition = "TEXT" to ensure the database 
    // allocates enough space to store your JSON metrics payload.
    @Column(columnDefinition = "TEXT", nullable = false)
    private String metrics;
 
    @Column(name = "generated_date", nullable = false, updatable = false)
    private LocalDateTime generatedDate;

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public ReportScope getScope() {
		return scope;
	}

	public void setScope(ReportScope scope) {
		this.scope = scope;
	}

	public String getMetrics() {
		return metrics;
	}

	public void setMetrics(String metrics) {
		this.metrics = metrics;
	}

	public LocalDateTime getGeneratedDate() {
		return generatedDate;
	}

	public void setGeneratedDate(LocalDateTime generatedDate) {
		this.generatedDate = generatedDate;
	}
 
    // Automatically stamps the exact time the report was generated
    
}