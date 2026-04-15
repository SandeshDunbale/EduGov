package com.project.edugov.controller;

import com.project.edugov.model.AuditLog;
import com.project.edugov.service.AuditServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditServiceImpl auditService;

    public AuditLogController(AuditServiceImpl auditService) {
        this.auditService = auditService;
    }

    // The Government Auditor will call this to see all system actions
    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getSystemLogs() {
        List<AuditLog> logs = auditService.getAllLogs();
        return ResponseEntity.ok(logs);
    }
}