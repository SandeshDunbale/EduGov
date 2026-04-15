package com.project.edugov.controller;
 
import java.util.List;
 
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
 
import com.project.edugov.dto.*;
import com.project.edugov.model.RequestStatus;
import com.project.edugov.service.ResourceRequestService;
import com.project.edugov.service.AuditServiceImpl;
 
import jakarta.validation.Valid;
 
@Slf4j
@RestController
@RequestMapping("/api/requests")
public class ResourceRequestController {
 
    private final ResourceRequestService service;
    private final ModelMapper mapper;
 
    @Autowired
    private AuditServiceImpl auditService;
 
    public ResourceRequestController(ResourceRequestService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
        log.info("ResourceRequestController initialized");
    }
 
    // =================================================
    // Submit RESOURCE request 
    // =================================================
    @PostMapping("/resource")
    public ResponseEntity<ResourceRequestResponse> submitResource(
            @Valid @RequestBody SubmitResourceRequest req) {
 
        auditService.logAction("SUBMIT_RESOURCE_REQUEST", "RESOURCE_ID_" + req.resourceId());
 
        log.info("API CALL: Submit Resource Request → requesterId={}, resourceId={}, qty={}",
                req.requesterUserId(), req.resourceId(), req.quantity());
 
        var saved = service.submitResourceRequest(
                req.requesterUserId(),
                req.resourceId(),
                req.quantity()
        );
 
        log.debug("Resource request created successfully → requestId={}", saved.getRequestId());
 
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(saved, ResourceRequestResponse.class));
    }
 
    // =================================================
    // Submit INFRASTRUCTURE request 
    // =================================================
    @PostMapping("/infrastructure")
    public ResponseEntity<InfrastructureRequestResponse> submitInfrastructure(
            @Valid @RequestBody SubmitInfrastructureRequest req) {
 
        auditService.logAction("SUBMIT_INFRASTRUCTURE_REQUEST", "INFRA_ID_" + req.infraId());
 
        log.info("API CALL: Submit Infrastructure Request → requesterId={}, infraId={}",
                req.requesterUserId(), req.infraId());
 
        var saved = service.submitInfrastructureRequest(
                req.requesterUserId(),
                req.infraId()
        );
 
        log.debug("Infrastructure request created successfully → requestId={}", saved.getRequestId());
 
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(saved, InfrastructureRequestResponse.class));
    }
 
    // =================================================
    // Approve request
    // =================================================
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Long id,
            @RequestParam Long approverUserId) {
 
        auditService.logAction("APPROVE_REQUEST", "REQUEST_ID_" + id);
 
        log.info("API CALL: Approve Request → requestId={}, approverUserId={}", id, approverUserId);
 
        var updated = service.approve(id, approverUserId);
 
        log.debug("Request approved successfully → requestId={}", id);
 
        return ResponseEntity.ok(mapToProperResponse(updated));
    }
 
    // =================================================
    // Decline request
    // =================================================
    @PostMapping("/{id}/decline")
    public ResponseEntity<?> decline(
            @PathVariable Long id,
            @RequestParam Long approverUserId,
            @RequestParam(required = false) String reason) {
 
        auditService.logAction("DECLINE_REQUEST", "REQUEST_ID_" + id);
 
        log.warn("API CALL: Decline Request → requestId={}, approverUserId={}, reason={}",
                id, approverUserId, reason);
 
        var updated = service.decline(id, approverUserId, reason);
 
        log.debug("Request declined → requestId={}", id);
 
        return ResponseEntity.ok(mapToProperResponse(updated));
    }
 
    // =================================================
    // Get request by ID
    // =================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        log.info("API CALL: Fetch Request by ID → {}", id);
 
        var rr = service.getById(id);
 
        return ResponseEntity.ok(mapToProperResponse(rr));
    }
 
    // =================================================
    // List requests by status
    // =================================================
    @GetMapping
    public List<?> listByStatus(@RequestParam RequestStatus status) {
 
        log.info("API CALL: List Requests by Status → {}", status);
 
        return service.listByStatus(status)
                .stream()
                .map(this::mapToProperResponse)
                .toList();
    }
 
    // =================================================
    // List requests by requester
    // =================================================
    @GetMapping("/by-requester/{userId}")
    public List<?> listByRequester(@PathVariable Long userId) {
 
        log.info("API CALL: List Requests by Requester → userId={}", userId);
 
        return service.listByRequester(userId)
                .stream()
                .map(this::mapToProperResponse)
                .toList();
    }
 
    // =================================================
    // Helper: map based on request type
    // =================================================
    private Object mapToProperResponse(Object rr) {
        var request = (com.project.edugov.model.ResourceRequest) rr;
 
        return switch (request.getItemType()) {
            case RESOURCE -> mapper.map(request, ResourceRequestResponse.class);
            case INFRASTRUCTURE -> mapper.map(request, InfrastructureRequestResponse.class);
        };
    }
}