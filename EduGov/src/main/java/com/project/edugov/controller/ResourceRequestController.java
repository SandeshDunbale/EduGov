package com.project.edugov.controller;

import static com.project.edugov.dto.DtoMappers.toDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.edugov.dto.DtoMappers;
import com.project.edugov.dto.ResourceRequestResponse;
import com.project.edugov.dto.SubmitInfrastructureRequest;
import com.project.edugov.dto.SubmitResourceRequest;
import com.project.edugov.model.RequestStatus;
import com.project.edugov.service.ResourceRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/requests")
public class ResourceRequestController {

    private final ResourceRequestService rrService;

    public ResourceRequestController(ResourceRequestService rrService) {
        this.rrService = rrService;
    }

    /** Submit a resource request (Student/Faculty) */
    @PostMapping("/resource")
    public ResponseEntity<ResourceRequestResponse> submitResource(@Valid @RequestBody SubmitResourceRequest req) {
        var saved = rrService.submitResourceRequest(req.requesterUserId(), req.resourceId(), req.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    /** Submit an infrastructure request (Student/Faculty) */
    @PostMapping("/infrastructure")
    public ResponseEntity<ResourceRequestResponse> submitInfra(@Valid @RequestBody SubmitInfrastructureRequest req) {
        var saved = rrService.submitInfrastructureRequest(req.requesterUserId(), req.infraId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    /** Approve a request (University Admin) */
    @PostMapping("/{id}/approve")
    public ResourceRequestResponse approve(@PathVariable Long id, @RequestParam Long approverUserId) {
        return toDto(rrService.approve(id, approverUserId));
    }

    /** Decline a request (University Admin) */
    @PostMapping("/{id}/decline")
    public ResourceRequestResponse decline(@PathVariable Long id,
                                           @RequestParam Long approverUserId,
                                           @RequestParam(required = false) String reason) {
        return toDto(rrService.decline(id, approverUserId, reason));
    }

    /** Approval queue by status (paged) */
    @GetMapping
    public Page<ResourceRequestResponse> listByStatus(@RequestParam RequestStatus status, Pageable pageable) {
        return rrService.listByStatus(status, pageable).map(DtoMappers::toDto);
    }

    /** Requests by requester (paged) */
    @GetMapping("/by-requester/{userId}")
    public Page<ResourceRequestResponse> listByRequester(@PathVariable("userId") Long requesterUserId,
                                                         Pageable pageable) {
        return rrService.listByRequester(requesterUserId, pageable).map(DtoMappers::toDto);
    }

    /** Get a specific request */
    @GetMapping("/{id}")
    public ResourceRequestResponse get(@PathVariable Long id) {
        return toDto(rrService.getById(id));
    }
}