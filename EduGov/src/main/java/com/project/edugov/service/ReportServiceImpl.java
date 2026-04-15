package com.project.edugov.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.project.edugov.dto.ReportDTO;
import com.project.edugov.model.Report;
import com.project.edugov.model.ReportScope;
import com.project.edugov.model.ProjectStatus; 
import com.project.edugov.model.Status; // 🎉 FIX: Imported your shared Status enum

import com.project.edugov.repository.ReportRepository;
import com.project.edugov.repository.ResearchProjectRepository;

import lombok.RequiredArgsConstructor;

import com.project.edugov.repository.GrantRepository;
import com.project.edugov.repository.ProgramRepository; 

@Service
public class ReportServiceImpl implements ReportService {

	private final ReportRepository reportRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final ResearchProjectRepository projectRepository;
	private final GrantRepository grantRepository;
	private final ProgramRepository programRepository; 
	private final NotificationService notificationService;

	public ReportServiceImpl(ReportRepository reportRepository, 
			ResearchProjectRepository projectRepository,
			GrantRepository grantRepository, 
			ProgramRepository programRepository,NotificationService notificationService) {
		this.reportRepository = reportRepository;
		this.projectRepository = projectRepository;
		this.grantRepository = grantRepository;
		this.programRepository = programRepository;
		this.notificationService=notificationService;
	}

	// 🔁 DTO → ENTITY
	private Report convertToEntity(ReportDTO dto) {
		Report report = new Report();
		report.setReportId(dto.getReportId());
		
		if (dto.getScope() != null) { 
			report.setScope(ReportScope.valueOf(dto.getScope().toUpperCase())); 
		}
		
		report.setMetrics(dto.getMetrics());
		
		if (dto.getGeneratedDate() != null) { 
			report.setGeneratedDate(dto.getGeneratedDate().atStartOfDay()); 
		}
		return report;
	}

	// 🔁 ENTITY → DTO
	private ReportDTO convertToDTO(Report report) {
		ReportDTO dto = new ReportDTO();
		dto.setReportId(report.getReportId());
		
		if (report.getScope() != null) { 
			dto.setScope(report.getScope().name()); 
		}
		
		dto.setMetrics(report.getMetrics());
		
		if (report.getGeneratedDate() != null) { 
			dto.setGeneratedDate(report.getGeneratedDate().toLocalDate()); 
		}
		return dto;
	}

	// --- MAIN METHODS ---

	@Override
	public ReportDTO generateReportByScope(ReportScope scope) {
		Map<String, Object> metrics = new HashMap<>();

		if (scope == ReportScope.PROGRAM) {
			metrics.put("totalPrograms", programRepository.count());
            
			// 🎉 FIX: Now using the 'Status' enum you specified!
			metrics.put("activePrograms", programRepository.countByStatus(Status.ACTIVE));
			metrics.put("inactivePrograms", programRepository.countByStatus(Status.INACTIVE));
            
		} else if (scope == ReportScope.PROJECT) {
			metrics.put("totalProjects", projectRepository.count());
			metrics.put("completedProjects", projectRepository.countByStatus(ProjectStatus.COMPLETED));
            
			long inProgress = projectRepository.countByStatus(ProjectStatus.SUBMITTED) + 
			                  projectRepository.countByStatus(ProjectStatus.UNDER_REVIEW);
			metrics.put("inProgressProjects", inProgress);
            
		} else if (scope == ReportScope.GRANT) {
			metrics.put("totalGrantCount", grantRepository.count());
			Double totalMoney = grantRepository.sumTotalGrantAmount();
			metrics.put("totalGrantAmount", totalMoney != null ? totalMoney : 0.0);
		}

		Report report = new Report();
		report.setScope(scope);
		report.setGeneratedDate(LocalDateTime.now());

		try {
			report.setMetrics(objectMapper.writeValueAsString(metrics));
		} catch (JsonProcessingException e) {
			report.setMetrics("{}");
			e.printStackTrace();
		}

		return convertToDTO(reportRepository.save(report));
	}

	@Override
	public ReportDTO generateReport(ReportDTO dto) {
		Report report = convertToEntity(dto);
		report.setGeneratedDate(LocalDateTime.now());
		return convertToDTO(reportRepository.save(report));
	}

	@Override
	public List<ReportDTO> getAllReports() {
		return reportRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Override
	public List<ReportDTO> getReportsByScope(String scope) {
		ReportScope enumScope = ReportScope.valueOf(scope.toUpperCase());
		return reportRepository.findByScope(enumScope).stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Override
	public List<ReportDTO> getReportsByDateRange(LocalDate start, LocalDate end) {
		LocalDateTime startOfDay = start.atStartOfDay();
		LocalDateTime endOfDay = end.atTime(23, 59, 59);

		return reportRepository.findByGeneratedDateBetween(startOfDay, endOfDay).stream().map(this::convertToDTO)
				.collect(Collectors.toList());
	}
}