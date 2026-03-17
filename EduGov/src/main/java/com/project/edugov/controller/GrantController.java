package com.project.edugov.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.edugov.model.Grant;
import com.project.edugov.model.GrantApplication;
import com.project.edugov.model.GrantStatus;
import com.project.edugov.service.GrantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/grants")
@RequiredArgsConstructor
public class GrantController {

    private final GrantService grantService;

    @PostMapping("/apply/{projectId}")
    public ResponseEntity<GrantApplication> applyForGrant(
            @Valid @RequestBody GrantApplication application,
            @PathVariable Long projectId,
            @RequestParam Long facultyId) {
        
        GrantApplication submittedApp = grantService.applyForGrant(application, projectId, facultyId);
        return new ResponseEntity<>(submittedApp, HttpStatus.CREATED);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<GrantApplication>> getPendingApplications() {
        return ResponseEntity.ok(grantService.getPendingApplications());
    }

    @PostMapping("/approve/{applicationId}")
    public ResponseEntity<Grant> approveGrant(
            @PathVariable Long applicationId,
            @RequestParam Long userId,
            @RequestParam GrantStatus decision) {
        
        Grant result = grantService.approveGrantApplication(applicationId, userId, decision);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/{facultyId}")
    public ResponseEntity<List<GrantApplication>> getHistory(@PathVariable Long facultyId) {
        return ResponseEntity.ok(grantService.getApplicationHistoryByFaculty(facultyId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Grant> getGrantDetails(@PathVariable Long projectId) {
        return ResponseEntity.ok(grantService.getGrantByProjectId(projectId));
    }
}

