package com.project.edugov.service;

import java.time.LocalDateTime; // Required for manual timestamp

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.project.edugov.service.AuditLogService;
import com.project.edugov.model.AuditLog;
import com.project.edugov.model.User;
import com.project.edugov.repository.AuditLogRepository;
import com.project.edugov.repository.UserRepository;

@Service
public class AuditServiceImpl implements AuditLogService{

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    // 1. For actions where the user is already logged in (e.g., Approving a student)
    public void logAction(String action, String resource) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String email = auth.getName(); 
            
            userRepository.findByEmail(email).ifPresentOrElse(
                user -> saveLog(user, action, resource),
                () -> logger.error("Audit fail: User email {} not found in DB", email)
            );
        } else {
            logger.warn("Audit fail: Attempted to log action '{}' without authentication context", action);
        }
    }

    // 2. For actions where the token isn't set yet (e.g., Login)
    public void logActionForUser(User user, String action, String resource) {
        saveLog(user, action, resource);
    }

    // 👉 THE FIX: Manually setting the timestamp
    private void saveLog(User user, String action, String resource) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setResource(resource);
        
        // Since the model cannot use @PrePersist, we set it here manually
        auditLog.setTimestamp(LocalDateTime.now()); 
        
        auditLogRepository.save(auditLog);
        logger.debug("AuditLog saved for User {}: {} on {}", user.getEmail(), action, resource);
    }
    
    // For the Auditor Controller to fetch the logs
    public java.util.List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }
}