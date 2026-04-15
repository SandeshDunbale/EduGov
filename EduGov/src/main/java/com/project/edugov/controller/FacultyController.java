package com.project.edugov.controller;

import com.project.edugov.dto.FacultyDTO;
import com.project.edugov.dto.FacultyResponseDTO;
import com.project.edugov.model.Status;
import com.project.edugov.service.FacultyService;
import com.project.edugov.service.AuditServiceImpl;
import com.project.edugov.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faculty")
@RequiredArgsConstructor
@Slf4j
public class FacultyController {

    private final FacultyService facultyService;
    private final AuditServiceImpl auditService;

    @PostMapping("/register")
    public ResponseEntity<FacultyResponseDTO> register(@Valid @RequestBody FacultyDTO dto) {
        auditService.logAction("REGISTER_FACULTY", "FACULTY_EMAIL_" + dto.getEmail());
        log.info("REST request to register faculty: {}", dto.getEmail());
        return new ResponseEntity<>(facultyService.registerFaculty(dto), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FacultyResponseDTO>> getByStatus(@RequestParam(required = false) Status status) {
        log.info(" get faculty list by status: {}", status);
        return ResponseEntity.ok(facultyService.getFacultyByStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponseDTO> getById(@PathVariable Long id) {
        log.info(" get faculty by ID: {}", id);
        return facultyService.getFacultyById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + id));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<FacultyResponseDTO> update(@PathVariable Long id, @Valid @RequestBody FacultyDTO dto) {
        auditService.logAction("UPDATE_FACULTY", "FACULTY_ID_" + id);
        log.info(" request to update faculty: {}", id);
        return ResponseEntity.ok(facultyService.updateFaculty(id, dto));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<FacultyResponseDTO> approve(@PathVariable Long id) {
        auditService.logAction("APPROVE_FACULTY", "FACULTY_ID_" + id);
        log.info(" approve faculty: {}", id);
        return ResponseEntity.ok(facultyService.approveFaculty(id));
    }

    @PatchMapping("/{id}/decline")
    public ResponseEntity<FacultyResponseDTO> decline(@PathVariable Long id) {
        auditService.logAction("DECLINE_FACULTY", "FACULTY_ID_" + id);
        log.info(" decline faculty: {}", id);
        return ResponseEntity.ok(facultyService.declineFaculty(id));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        auditService.logAction("DELETE_FACULTY", "FACULTY_ID_" + id);
        log.warn("REST request to delete faculty: {}", id);
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }
}