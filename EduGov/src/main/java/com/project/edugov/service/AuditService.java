package com.project.edugov.service;

import com.project.edugov.model.*;
import com.project.edugov.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    @Autowired private AuditRepository auditRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private AuditLogRepository auditLogRepository;

    // [32] Get all audits
    public List<Audit> getAllAudits() {
        return auditRepository.findAll();
    }

    // Helper for Single Audit
    public Audit getAuditById(Long id) {
        return auditRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit record not found with ID: " + id));
    }

    // [31] Create a new audit
    @Transactional
    public Audit createAudit(Audit audit, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        audit.setOfficer(creator);
        // Defaulting to today's date if not provided in the body
        if (audit.getDate() == null) audit.setDate(LocalDate.now());
        if (audit.getStatus() == null) audit.setStatus("SCHEDULED");
        
        Audit savedAudit = auditRepository.save(audit);
        createLog(creator, "CREATE_AUDIT", "AUDIT_ID: " + savedAudit.getAuditId());
        return savedAudit;
    }

    // [33] Update audit details
    @Transactional
    public Audit updateAudit(Long id, Audit auditDetails) {
        Audit audit = getAuditById(id);
        
        // Match these with your model fields
        audit.setScope(auditDetails.getScope());
        audit.setDate(auditDetails.getDate());
        audit.setStatus(auditDetails.getStatus());
        audit.setFindings(auditDetails.getFindings());
        
        return auditRepository.save(audit);
    }

    // [34] Delete audit
    @Transactional
    public void deleteAudit(Long id) {
        if (!auditRepository.existsById(id)) {
            throw new RuntimeException("Audit not found with ID: " + id);
        }
        auditRepository.deleteById(id);
        logger.warn("Audit record ID: {} has been deleted", id);
    }

    // ORIGINAL LOGIC PRESERVED: Review Audit
    @Transactional
    public Audit reviewAudit(Long auditId, String status, String findings, Long auditorId) {
        logger.info("Initiating audit review for Audit ID: {} by User ID: {}", auditId, auditorId);

        User auditor = userRepository.findById(auditorId)
                .orElseThrow(() -> new RuntimeException("Auditor not found with ID: " + auditorId));

        if (auditor.getRole() != Role.GOVT_AUDITOR) {
            throw new RuntimeException("Access Denied: Only Government Auditors can perform this action.");
        }

        Audit audit = getAuditById(auditId);
        audit.setStatus(status);
        audit.setFindings(findings);
        audit.setOfficer(auditor);
        
        Audit updatedAudit = auditRepository.save(audit);

        if ("DENIED".equalsIgnoreCase(status)) {
            triggerCorrectionNotification(updatedAudit, findings);
        }

        createLog(auditor, "REVIEW_AUDIT", "AUDIT_ID: " + auditId + " STATUS: " + status);
        return updatedAudit;
    }

    private void triggerCorrectionNotification(Audit audit, String findings) {
        List<User> managers = userRepository.findByRole(Role.PROG_MANAGER);
        if (!managers.isEmpty()) {
            Notification notification = new Notification();
//            notification.setRecipient(managers.get(0)); 
//            notification.setEntityID(audit.getAuditId());
            notification.setMessage("Action Required: Audit for '" + audit.getScope() + "' was denied. Reason: " + findings);
//            notification.setCategory(NotificationCategory.COMPLIANCE);
            notification.setStatus("UNREAD");
            notification.setCreatedDate(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    private void createLog(User user, String action, String resource) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(action);
        log.setResource(resource);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }
}