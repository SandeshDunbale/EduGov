package com.project.edugov.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.edugov.dto.AuditReviewDTO;
import com.project.edugov.model.Audit;
import com.project.edugov.service.AuditService;
import com.project.edugov.service.AuditServiceImpl;

@RestController
@RequestMapping("/api/audits")
@CrossOrigin(origins = "*")
public class AuditController {

    private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuditServiceImpl auditLogService;

    // [31] Create a new audit
    @PostMapping
    public ResponseEntity<Audit> createAudit(@RequestBody Audit audit, @RequestHeader("X-User-Id") Long userId) {
        auditLogService.logAction("CREATE_AUDIT", "USER_ID_" + userId);
        return ResponseEntity.ok(auditService.createAudit(audit, userId));
    }

    // [32] Get all audits
    @GetMapping
    public ResponseEntity<List<Audit>> getAllAudits() {
        return ResponseEntity.ok(auditService.getAllAudits());
    }

    // Helper: Get Single Audit
    @GetMapping("/{id}")
    public ResponseEntity<Audit> getAudit(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.getAuditById(id));
    }

    // [33] Update audit details
    @PutMapping("/update/{id}")
    public ResponseEntity<Audit> updateAudit(@PathVariable Long id, @RequestBody Audit audit) {
        auditLogService.logAction("UPDATE_AUDIT", "AUDIT_ID_" + id);
        return ResponseEntity.ok(auditService.updateAudit(id, audit));
    }

    // [34] Delete audit
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAudit(@PathVariable Long id) {
        auditLogService.logAction("DELETE_AUDIT", "AUDIT_ID_" + id);
        auditService.deleteAudit(id);
        return ResponseEntity.noContent().build();
    }

    // ORIGINAL LOGIC PRESERVED: Patch review
    @PatchMapping("/{id}/review")
    public ResponseEntity<Audit> reviewAudit(
            @PathVariable Long id,
            @RequestBody AuditReviewDTO reviewDto, 
            @RequestHeader("X-User-Id") Long auditorId) {
        
        auditLogService.logAction("REVIEW_AUDIT", "AUDIT_ID_" + id);
        Audit updatedAudit = auditService.reviewAudit(id, reviewDto.getStatus(), reviewDto.getFindings(), auditorId);
        return ResponseEntity.ok(updatedAudit);
    }
}