package com.project.edugov.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.edugov.model.*;
import com.project.edugov.repository.*;
import com.project.edugov.dto.ComplianceRecordDTO;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplianceService {

    private static final Logger logger = LoggerFactory.getLogger(ComplianceService.class);

    @Autowired private ComplianceRepository complianceRepo;
    @Autowired private GrantApplicationRepository applicationRepo;
    @Autowired private GrantRepository grantRepo;
    @Autowired private ProgramRepository programRepo;
    @Autowired private StudentRepository studentRepo;
    @Autowired private UserRepository userRepo;

    // --- AUTO GENERATION LOGIC ---
    public void generateCompliance(Long officerId) {
        User officer = userRepo.findById(officerId).orElseThrow(() -> new RuntimeException("Officer not found"));

        // LOGIC 1: Approved/Completed Apps with no Grant record
        applicationRepo.findByStatusIn(Arrays.asList(GrantApplicationStatus.APPROVED, GrantApplicationStatus.COMPLETED))
            .forEach(app -> {
                if (grantRepo.findByProject(app.getProject()).isEmpty()) {
                    saveCompliance(app.getProject().getProjectId(), "PROJECT", "Missing Grant for " + app.getStatus() + " app.", officer);
                }
            });

        // LOGIC 2: Inactive Program with Students
        programRepo.findByStatus(Status.INACTIVE).forEach(p -> {
            if (studentRepo.count() > 0) {
                saveCompliance(p.getProgramID(), "PROGRAM", "Students in inactive program.", officer);
            }
        });

        // LOGIC 3: Over-funding
        grantRepo.findAll().forEach(grant -> {
            applicationRepo.findByProject(grant.getProject()).ifPresent(app -> {
                if (app.getRequestedAmount() != null && grant.getAmount().compareTo(app.getRequestedAmount()) > 0) {
                    saveCompliance(grant.getProject().getProjectId(), "PROJECT", "Over-funding detected.", officer);
                }
            });
        });
    }

    private void saveCompliance(Long id, String type, String notes, User officer) {
        if (!complianceRepo.existsByEntityIdAndEntityType(id, type)) {
            ComplianceRecord r = new ComplianceRecord();
            r.setEntityId(id); r.setEntityType(type); r.setNotes(notes);
            r.setDate(LocalDate.now()); r.setOfficer(officer);
            r.setResult(ComplianceRecordStatus.UNDER_REVIEW);
            complianceRepo.save(r);
        }
    }

    // --- CRUD OPERATIONS (27-30) ---
    public ComplianceRecordDTO createManual(ComplianceRecord record, Long officerId) {
        User officer = userRepo.findById(officerId).orElseThrow(() -> new RuntimeException("User not found"));
        record.setOfficer(officer);
        record.setDate(LocalDate.now());
        return convertToDTO(complianceRepo.save(record));
    }

    public List<ComplianceRecordDTO> getAllCompliance() {
        return complianceRepo.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ComplianceRecordDTO updateCompliance(Long id, ComplianceRecord details) {
        ComplianceRecord existing = complianceRepo.findById(id).orElseThrow(() -> new RuntimeException("Not Found"));
        existing.setNotes(details.getNotes());
        existing.setResult(details.getResult());
        return convertToDTO(complianceRepo.save(existing));
    }

    public void deleteCompliance(Long id) {
        complianceRepo.deleteById(id);
    }

    private ComplianceRecordDTO convertToDTO(ComplianceRecord record) {
        ComplianceRecordDTO dto = new ComplianceRecordDTO();
        dto.setComplianceId(record.getComplianceId());
        dto.setEntityId(record.getEntityId());
        dto.setEntityType(record.getEntityType());
        dto.setResult(record.getResult());
        dto.setDate(record.getDate());
        dto.setNotes(record.getNotes());
        dto.setOfficerName(record.getOfficer().getName());
        return dto;
    }
}