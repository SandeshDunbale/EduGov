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
import com.project.edugov.dto.ResourceAllocateRequest;
import com.project.edugov.dto.ResourceCreateRequest;
import com.project.edugov.dto.ResourceResponse;
import com.project.edugov.dto.ResourceStatusUpdateRequest;
import com.project.edugov.model.ResourceStatus;
import com.project.edugov.service.ResourceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    /** Create a new resource (Program-scoped) */
    @PostMapping
    public ResponseEntity<ResourceResponse> create(@Valid @RequestBody ResourceCreateRequest req) {
        var saved = resourceService.create(req.programId(), req.type(), req.quantity(), req.status());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    /** Get a resource by id */
    @GetMapping("/{id}")
    public ResourceResponse get(@PathVariable Long id) {
        return toDto(resourceService.getById(id));
    }

    /** List resources by status (paged). If status not given, defaults to AVAILABLE page 0. */
    @GetMapping
    public Page<ResourceResponse> listByStatus(@RequestParam(required = false) ResourceStatus status,
                                               Pageable pageable) {
        var effective = (status != null) ? status : ResourceStatus.AVAILABLE;
        return resourceService.findByStatus(effective, pageable).map(DtoMappers::toDto);
    }

    /** Update only the status of a resource */
    @PatchMapping("/{id}/status")
    public ResourceResponse updateStatus(@PathVariable Long id,
                                         @Valid @RequestBody ResourceStatusUpdateRequest req) {
        return toDto(resourceService.updateStatus(id, req.status()));
    }

    /** Allocate quantity from a resource (and mark ALLOCATED) */
    @PostMapping("/{id}/allocate")
    public ResourceResponse allocate(@PathVariable Long id,
                                     @Valid @RequestBody ResourceAllocateRequest req) {
        return toDto(resourceService.allocate(id, req.quantity()));
    }
}