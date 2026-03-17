package com.project.edugov.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.edugov.dto.GrantApplicationDTO; // Import DTO
import com.project.edugov.dto.GrantResponseDTO;    // Import DTO
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

    /**
     * Faculty applies for a grant. Returns clean Application DTO.
     */
    @PostMapping("/apply/{projectId}")
    public ResponseEntity<GrantApplicationDTO> applyForGrant(
            @Valid @RequestBody GrantApplication application,
            @PathVariable Long projectId,
            @RequestParam Long facultyId) {
        
        // Service now returns GrantApplicationDTO
        GrantApplicationDTO submittedApp = grantService.applyForGrant(application, projectId, facultyId);
        return new ResponseEntity<>(submittedApp, HttpStatus.CREATED);
    }

    /**
     * Program Manager views all pending applications as clean DTOs.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<GrantApplicationDTO>> getPendingApplications() {
        return ResponseEntity.ok(grantService.getPendingApplications());
    }

    /**
     * Program Manager approves/rejects. Returns GrantResponseDTO with Manager name.
     */
    @PostMapping("/approve/{applicationId}")
    public ResponseEntity<GrantResponseDTO> approveGrant(
            @PathVariable Long applicationId,
            @RequestParam Long userId,
            @RequestParam GrantStatus decision) {
        
        // Service now returns GrantResponseDTO
        GrantResponseDTO result = grantService.approveGrantApplication(applicationId, userId, decision);
        return ResponseEntity.ok(result);
    }

    /**
     * Faculty views their history as a clean DTO list.
     */
    @GetMapping("/history/{facultyId}")
    public ResponseEntity<List<GrantApplicationDTO>> getHistory(@PathVariable Long facultyId) {
        return ResponseEntity.ok(grantService.getApplicationHistoryByFaculty(facultyId));
    }

    /**
     * Get final disbursement details. Returns clean GrantResponseDTO.
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<GrantResponseDTO> getGrantDetails(@PathVariable Long projectId) {
        return ResponseEntity.ok(grantService.getGrantByProjectId(projectId));
    }
}