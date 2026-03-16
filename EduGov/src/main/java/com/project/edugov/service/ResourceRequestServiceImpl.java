// src/com.project.edugov/service/impl/ResourceRequestServiceImpl.java
package com.project.edugov.service;

import com.project.edugov.model.*;

import com.project.edugov.repository.*;
import com.project.edugov.service.InfrastructureService;
import com.project.edugov.service.ResourceRequestService;
import com.project.edugov.service.ResourceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class ResourceRequestServiceImpl implements ResourceRequestService {

    private final ResourceRequestRepository requestRepo;
    private final ResourceRepository resourceRepo;
    private final InfrastructureRepository infraRepo;
    private final UserRepository userRepo;

    // Reuse your existing services for allocation/in use transitions
    private final ResourceService resourceService;
    private final InfrastructureService infrastructureService;

    public ResourceRequestServiceImpl(ResourceRequestRepository requestRepo,
                                      ResourceRepository resourceRepo,
                                      InfrastructureRepository infraRepo,
                                      UserRepository userRepo,
                                      ResourceService resourceService,
                                      InfrastructureService infrastructureService) {
        this.requestRepo = requestRepo;
        this.resourceRepo = resourceRepo;
        this.infraRepo = infraRepo;
        this.userRepo = userRepo;
        this.resourceService = resourceService;
        this.infrastructureService = infrastructureService;
    }

    @Override
    public ResourceRequest submitResourceRequest(Long requesterUserId, Long resourceId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");

        User requester = userRepo.findById(requesterUserId)
                .orElseThrow(() -> new EntityNotFoundException("Requester not found: " + requesterUserId));
        Resource resource = resourceRepo.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found: " + resourceId));

        ResourceRequest rr = ResourceRequest.builder()
                .requester(requester)
                .resource(resource)
                .itemType(RequestItemType.RESOURCE)
                .quantity(quantity)
                .status(RequestStatus.SUBMITTED)
                .build();

        return requestRepo.save(rr);
    }

    @Override
    public ResourceRequest submitInfrastructureRequest(Long requesterUserId, Long infraId) {
        User requester = userRepo.findById(requesterUserId)
                .orElseThrow(() -> new EntityNotFoundException("Requester not found: " + requesterUserId));
        Infrastructure infra = infraRepo.findById(infraId)
                .orElseThrow(() -> new EntityNotFoundException("Infrastructure not found: " + infraId));

        ResourceRequest rr = ResourceRequest.builder()
                .requester(requester)
                .infrastructure(infra)
                .itemType(RequestItemType.INFRASTRUCTURE)
                .status(RequestStatus.SUBMITTED)
                .build();

        return requestRepo.save(rr);
    }

    @Override
    public ResourceRequest approve(Long requestId, Long approverUserId) {
        ResourceRequest rr = getById(requestId);
        if (rr.getStatus() == RequestStatus.APPROVED || rr.getStatus() == RequestStatus.DECLINED) {
            return rr; // idempotent
        }

        User approver = userRepo.findById(approverUserId)
                .orElseThrow(() -> new EntityNotFoundException("Approver user not found: " + approverUserId));

        // Perform domain-side effects based on item type
        if (rr.getItemType() == RequestItemType.RESOURCE) {
            // decrement quantity & set status to ALLOCATED on Resource
            int qty = rr.getQuantity() != null ? rr.getQuantity() : 0;
            resourceService.allocate(rr.getResource().getResourceId(), qty);
        } else if (rr.getItemType() == RequestItemType.INFRASTRUCTURE) {
            // mark infra IN_USE (you can extend to use booking windows)
            infrastructureService.markInUse(rr.getInfrastructure().getInfraId());
        }

        rr.setStatus(RequestStatus.APPROVED);
        rr.setApprovedBy(approver);
        rr.setDecisionAt(Instant.now());
        return requestRepo.save(rr);
    }

    @Override
    public ResourceRequest decline(Long requestId, Long approverUserId, String reasonOptional) {
        ResourceRequest rr = getById(requestId);
        if (rr.getStatus() == RequestStatus.APPROVED || rr.getStatus() == RequestStatus.DECLINED) {
            return rr; // idempotent
        }

        User approver = userRepo.findById(approverUserId)
                .orElseThrow(() -> new EntityNotFoundException("Approver user not found: " + approverUserId));

        rr.setStatus(RequestStatus.DECLINED);
        rr.setApprovedBy(approver);
        rr.setDecisionAt(Instant.now());
        // You can persist a declineReason column if you add it to the entity
        return requestRepo.save(rr);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResourceRequest> listByStatus(RequestStatus status, Pageable pageable) {
        return requestRepo.findByStatus(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResourceRequest> listByRequester(Long requesterUserId, Pageable pageable) {
        User requester = userRepo.findById(requesterUserId)
                .orElseThrow(() -> new EntityNotFoundException("Requester not found: " + requesterUserId));
        return requestRepo.findByRequester(requester, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceRequest getById(Long requestId) {
        return requestRepo.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("ResourceRequest not found: " + requestId));
    }
}