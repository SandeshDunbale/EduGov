/*
 * package com.project.edugov.service;
 * 
 * import java.time.LocalDate; import java.util.List; import
 * java.util.stream.Collectors;
 * 
 * import org.springframework.stereotype.Service;
 * 
 * import com.project.edugov.dto.ReportDTO; import
 * com.project.edugov.model.Report; import
 * com.project.edugov.repository.ReportRepository; //actual logic
 * 
 * @Service public class ReportServiceImpl implements ReportService {
 * 
 * private final ReportRepository repository;
 * 
 * public ReportServiceImpl(ReportRepository repository) { this.repository =
 * repository; }
 * 
 * // 🔁 DTO → ENTITY private Report convertToEntity(ReportDTO dto) {//API data
 * to database format Report report = new Report();
 * report.setReportId(dto.getReportId()); report.setScope(dto.getScope());
 * report.setMetrics(dto.getMetrics());
 * report.setGeneratedDate(dto.getGeneratedDate()); return report; }
 * 
 * // 🔁 ENTITY → DTO private ReportDTO convertToDTO(Report report) { //database
 * to API response ReportDTO dto = new ReportDTO();
 * dto.setReportId(report.getReportId()); dto.setScope(report.getScope());
 * dto.setMetrics(report.getMetrics());
 * dto.setGeneratedDate(report.getGeneratedDate()); return dto; }
 * 
 * @Override public ReportDTO generateReport(ReportDTO dto) { Report report =
 * convertToEntity(dto); report.setGeneratedDate(LocalDate.now()); return
 * convertToDTO(repository.save(report)); }
 * 
 * @Override public List<ReportDTO> getAllReports() { return
 * repository.findAll() .stream() .map(this::convertToDTO)
 * .collect(Collectors.toList()); }
 * 
 * @Override public List<ReportDTO> getReportsByScope(String scope) { return
 * repository.findByScope(scope) .stream() .map(this::convertToDTO)
 * .collect(Collectors.toList()); }
 * 
 * @Override public List<ReportDTO> getReportsByDateRange(LocalDate start,
 * LocalDate end) { return repository.findByGeneratedDateBetween(start, end)
 * .stream() .map(this::convertToDTO) .collect(Collectors.toList()); }
 * 
 * 
 * }
 */