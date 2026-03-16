package com.project.edugov.controller;

import static com.project.edugov.dto.DtoMappers.toDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.edugov.dto.DtoMappers;
import com.project.edugov.dto.InfrastructureCreateRequest;
import com.project.edugov.dto.InfrastructureResponse;
import com.project.edugov.dto.InfrastructureStatusUpdateRequest;
import com.project.edugov.model.Program;
import com.project.edugov.service.InfrastructureService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/infrastructure")
public class InfrastructureController {

    private final InfrastructureService infraService;

    public InfrastructureController(InfrastructureService infraService) {
        this.infraService = infraService;
    }

    /** Create Infrastructure for a Program */
    @PostMapping
    public ResponseEntity<InfrastructureResponse> create(@Valid @RequestBody InfrastructureCreateRequest req) {
        var saved = infraService.create(req.programId(), req.type(), req.location(), req.capacity(), req.status());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    /** Get Infrastructure by id */
    @GetMapping("/{id}")
    public InfrastructureResponse get(@PathVariable Long id) {
        return toDto(infraService.getById(id));
    }

    /** List Infrastructure by Program (paged) */
    @GetMapping
    public Page<InfrastructureResponse> listByProgram(@RequestParam Long programId, Pageable pageable) {
        var program = new Program();
        program.setProgramID(programId); // NOTE: your Program uses field 'programID' (capital D)
        return infraService.findByProgram(program, pageable).map(DtoMappers::toDto);
    }

    /** Update status (AVAILABLE / IN_USE / MAINTENANCE / RETIRED) */
    @PatchMapping("/{id}/status")
    public InfrastructureResponse updateStatus(@PathVariable Long id,
                                               @Valid @RequestBody InfrastructureStatusUpdateRequest req) {
        return toDto(infraService.updateStatus(id, req.status()));
    }

    /** Mark infrastructure IN_USE (e.g., upon approval of a booking/request) */
    @PostMapping("/{id}/use")
    public InfrastructureResponse markInUse(@PathVariable Long id) {
        return toDto(infraService.markInUse(id));
    }
}