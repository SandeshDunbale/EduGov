package com.project.edugov.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.project.edugov.dto.DeleteResourceResponse;
import com.project.edugov.dto.ResourceAllocateRequest;
import com.project.edugov.dto.ResourceCreateRequest;
import com.project.edugov.dto.ResourceResponse;
import com.project.edugov.dto.ResourceStatusUpdateRequest;
import com.project.edugov.dto.ResourceUpdateRequest;
import com.project.edugov.service.ResourceService;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final ModelMapper mapper;

    public ResourceController(ResourceService resourceService, ModelMapper mapper) {
        this.resourceService = resourceService;
        this.mapper = mapper;
        log.info("ResourceController initialized");
    }

    // ---------------------------------------------
    // CREATE RESOURCE
    // ---------------------------------------------
    @PostMapping
    public ResponseEntity<ResourceResponse> create(@Valid @RequestBody ResourceCreateRequest req) {
        log.info("API CALL: Create Resource → programId={}, type={}, quantity={}",
                 req.programId(), req.type(), req.quantity());

        var saved = resourceService.create(req.programId(), req.type(), req.quantity(), req.status());

        log.debug("Resource created successfully → resourceId={}", saved.getResourceId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(saved, ResourceResponse.class));
    }

    // ---------------------------------------------
    // GET RESOURCE BY ID
    // ---------------------------------------------
    @GetMapping("/{id}")
    public ResourceResponse get(@PathVariable Long id) {
        log.info("API CALL: Get Resource by ID → {}", id);

        var resource = resourceService.getById(id);

        log.debug("Fetched resource → {}", resource.getResourceId());

        return mapper.map(resource, ResourceResponse.class);
    }

    // ---------------------------------------------
    // UPDATE RESOURCE STATUS
    // ---------------------------------------------
    @PatchMapping("/{id}/status")
    public ResourceResponse updateStatus(@PathVariable Long id,
                                         @Valid @RequestBody ResourceStatusUpdateRequest req) {

        log.info("API CALL: Update Resource Status → id={}, newStatus={}", id, req.status());

        var updated = resourceService.updateStatus(id, req.status());

        log.debug("Resource status updated → id={}, status={}", id, updated.getStatus());

        return mapper.map(updated, ResourceResponse.class);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ResourceUpdateRequest req) {

        log.info("API CALL: Update Resource Details → id={}", id);

        var updated = resourceService.update(
                id,
                req.programId(),
                req.type(),
                req.quantity(),
                req.status()
        );

        return ResponseEntity.ok(mapper.map(updated, ResourceResponse.class));
    }

    // ---------------------------------------------
    // ALLOCATE RESOURCE QUANTITY
    // ---------------------------------------------
    @PostMapping("/{id}/allocate")
    public ResourceResponse allocate(@PathVariable Long id,
                                     @Valid @RequestBody ResourceAllocateRequest req) {

        log.info("API CALL: Allocate Resource → id={}, qty={}", id, req.quantity());

        var updated = resourceService.allocate(id, req.quantity());

        log.debug("Resource allocation completed → id={}, remainingQty={}",
                  id, updated.getQuantity());

        return mapper.map(updated, ResourceResponse.class);
    }

    // ---------------------------------------------
    // GET ALL RESOURCES
    // ---------------------------------------------
    @GetMapping("/all")
    public List<ResourceResponse> getAll() {
        log.info("API CALL: Fetch ALL Resources");

        var resources = resourceService.findAll();

        log.debug("Total resources fetched → count={}", resources.size());

        return resources.stream()
                .map(r -> mapper.map(r, ResourceResponse.class))
                .toList();
    }

    // ---------------------------------------------
    // DELETE RESOURCE
    // ---------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResourceResponse> delete(@PathVariable Long id) {
        log.warn("API CALL: Delete Resource → id={}", id);

        resourceService.delete(id);

        log.info("Resource deleted successfully → id={}", id);

        return ResponseEntity.ok(new DeleteResourceResponse(id, true, "Resource deleted"));
    }
}