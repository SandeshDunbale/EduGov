package com.project.edugov.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.edugov.dto.InfrastructureCreateRequest;
import com.project.edugov.dto.InfrastructureResponse;
import com.project.edugov.dto.InfrastructureStatusUpdateRequest;
import com.project.edugov.dto.InfrastructureUpdateRequest;
import com.project.edugov.service.InfrastructureService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/infrastructure")
public class InfrastructureController {

    private final InfrastructureService infraService;
    private final ModelMapper mapper;

    public InfrastructureController(InfrastructureService infraService, ModelMapper mapper) {
        this.infraService = infraService;
        this.mapper = mapper;
        log.info("InfrastructureController initialized");
    }

    // ---------------------------------------------
    // CREATE INFRASTRUCTURE
    // ---------------------------------------------
    @PostMapping
    public ResponseEntity<InfrastructureResponse> create(
            @Valid @RequestBody InfrastructureCreateRequest req) {

        log.info("API CALL: Create Infrastructure → programId={}, type={}, location={}",
                 req.programId(), req.type(), req.location());

        var saved = infraService.create(
                req.programId(),
                req.type(),
                req.location(),
                req.capacity(),
                req.status()
        );

        log.debug("Infrastructure created successfully → infraId={}", saved.getInfraId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(saved, InfrastructureResponse.class));
    }

    // ---------------------------------------------
    // GET INFRASTRUCTURE BY ID
    // ---------------------------------------------
    @GetMapping("/{id}")
    public InfrastructureResponse get(@PathVariable Long id) {
        log.info("API CALL: Get Infrastructure by ID → {}", id);

        var infra = infraService.getById(id);
        log.debug("Fetched infrastructure → {}", infra.getInfraId());

        return mapper.map(infra, InfrastructureResponse.class);
    }

    // ---------------------------------------------
    // GET ALL INFRASTRUCTURE
    // ---------------------------------------------
    @GetMapping("/all")
    public List<InfrastructureResponse> getAll() {
        log.info("API CALL: Fetch ALL Infrastructure");

        var list = infraService.findAll();
        log.debug("Total infrastructure count={}", list.size());

        return list.stream()
                .map(i -> mapper.map(i, InfrastructureResponse.class))
                .toList();
    }

    // ---------------------------------------------
    // UPDATE INFRASTRUCTURE STATUS
    // ---------------------------------------------
    @PatchMapping("/{id}/status")
    public InfrastructureResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody InfrastructureStatusUpdateRequest req) {

        log.info("API CALL: Update Infrastructure Status → id={}, newStatus={}",
                 id, req.status());

        var updated = infraService.updateStatus(id, req.status());

        log.debug("Infrastructure status updated → id={}, status={}",
                  updated.getInfraId(), updated.getStatus());

        return mapper.map(updated, InfrastructureResponse.class);
    }
    @PutMapping("/{id}")
    public ResponseEntity<InfrastructureResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody InfrastructureUpdateRequest req) {

        log.info("API CALL: Update Infrastructure Details → id={}", id);

        var updated = infraService.update(
                id,
                req.programId(),
                req.type(),
                req.location(),
                req.capacity(),
                req.status()
        );

        return ResponseEntity.ok(mapper.map(updated, InfrastructureResponse.class));
    }

    // ---------------------------------------------
    // DELETE INFRASTRUCTURE
    // ---------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.warn("API CALL: Delete Infrastructure → id={}", id);

        infraService.delete(id);

        log.info("Infrastructure deleted successfully → id={}", id);

        return ResponseEntity.noContent().build();
    }
}
