package com.project.edugov.service;

import java.time.Instant;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.edugov.model.*;
import com.project.edugov.repository.*;

@Slf4j
@Service
@Transactional
public class ResourceRequestServiceImpl implements ResourceRequestService {

    private final ResourceRequestRepository requestRepo;
    private final ResourceRepository resourceRepo;
    private final InfrastructureRepository infraRepo;
    private final UserRepository userRepo;

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

        log.info("ResourceRequestServiceImpl initialized");
    }

    // =================================================
    // Submit Resource Request
    // =================================================
    @Override
    public ResourceRequest submitResourceRequest(Long requesterUserId, Long resourceId, int quantity) {

        log.info("Submitting Resource Request → requesterId={}, resourceId={}, qty={}",
                 requesterUserId, resourceId, quantity);

        if (quantity <= 0) {
            log.error("Invalid quantity submitted → {}", quantity);
            throw new IllegalArgumentException("quantity must be > 0");
        }

        User requester = userRepo.findById(requesterUserId)
                .orElseThrow(() -> {
                    log.error("Invalid requester → {}", requesterUserId);
                    return new EntityNotFoundException("Requester not found: " + requesterUserId);
                });

        Resource resource = resourceRepo.findById(resourceId)
                .orElseThrow(() -> {
                    log.error("Resource not found → {}", resourceId);
                    return new EntityNotFoundException("Resource not found: " + resourceId);
                });

        ResourceRequest rr = ResourceRequest.builder()
                .requester(requester)
                .resource(resource)
                .itemType(RequestItemType.RESOURCE)
                .quantity(quantity)
                .status(RequestStatus.SUBMITTED)
                .build();

        ResourceRequest saved = requestRepo.save(rr);

        log.debug("Resource request saved → requestId={}", saved.getRequestId());

        return saved;
    }

    // =================================================
    // Submit Infrastructure Request
    // =================================================
    @Override
    public ResourceRequest submitInfrastructureRequest(Long requesterUserId, Long infraId) {

        log.info("Submitting Infrastructure Request → requesterId={}, infraId={}",
                 requesterUserId, infraId);

        User requester = userRepo.findById(requesterUserId)
                .orElseThrow(() -> {
                    log.error("Invalid requester → {}", requesterUserId);
                    return new EntityNotFoundException("Requester not found: " + requesterUserId);
                });

        Infrastructure infra = infraRepo.findById(infraId)
                .orElseThrow(() -> {
                    log.error("Infrastructure not found → {}", infraId);
                    return new EntityNotFoundException("Infrastructure not found: " + infraId);
                });

        ResourceRequest rr = ResourceRequest.builder()
                .requester(requester)
                .infrastructure(infra)
                .itemType(RequestItemType.INFRASTRUCTURE)
                .status(RequestStatus.SUBMITTED)
                .build();

        ResourceRequest saved = requestRepo.save(rr);

        log.debug("Infrastructure request saved → requestId={}", saved.getRequestId());

        return saved;
    }

    // =================================================
    // Approve Request
    // =================================================
    @Override
    public ResourceRequest approve(Long requestId, Long approverUserId) {

        log.info("Approving request → requestId={}, approverUserId={}", requestId, approverUserId);

        ResourceRequest rr = getById(requestId);

        if (rr.getStatus() == RequestStatus.APPROVED || rr.getStatus() == RequestStatus.DECLINED) {
            log.warn("Approve skipped → request already terminal. requestId={}, status={}", requestId, rr.getStatus());
            return rr;
        }

        if (rr.getStatus() != RequestStatus.SUBMITTED && rr.getStatus() != RequestStatus.IN_REVIEW) {
            log.error("Invalid approve transition → requestId={}, currentStatus={}", requestId, rr.getStatus());
            throw new IllegalStateException("Request must be SUBMITTED or IN_REVIEW to approve");
        }

        User approver = userRepo.findById(approverUserId)
                .orElseThrow(() -> {
                    log.error("Approver not found → {}", approverUserId);
                    return new EntityNotFoundException("Approver user not found: " + approverUserId);
                });

        if (rr.getItemType() == RequestItemType.RESOURCE) {
            int qty = rr.getQuantity();
            log.debug("Processing resource allocation → requestId={}, qty={}", requestId, qty);
            resourceService.allocate(rr.getResource().getResourceId(), qty);

        } else {
            log.debug("Marking infrastructure IN_USE → requestId={}", requestId);
            infrastructureService.markInUse(rr.getInfrastructure().getInfraId());
        }

        rr.setStatus(RequestStatus.APPROVED);
        rr.setApprovedBy(approver);
        rr.setDecisionAt(Instant.now());

        ResourceRequest saved = requestRepo.save(rr);

        log.info("Request approved successfully → requestId={}", saved.getRequestId());

        return saved;
    }

    // =================================================
    // Decline Request
    // =================================================
    @Override
    public ResourceRequest decline(Long requestId, Long approverUserId, String reasonOptional) {

        log.warn("Declining request → requestId={}, approverUserId={}, reason={}",
                 requestId, approverUserId, reasonOptional);

        ResourceRequest rr = getById(requestId);

        if (rr.getStatus() == RequestStatus.APPROVED || rr.getStatus() == RequestStatus.DECLINED) {
            log.warn("Decline skipped → request already terminal");
            return rr;
        }

        if (rr.getStatus() != RequestStatus.SUBMITTED && rr.getStatus() != RequestStatus.IN_REVIEW) {
            log.error("Invalid decline transition for requestId={} → currentStatus={}", requestId, rr.getStatus());
            throw new IllegalStateException("Request must be SUBMITTED or IN_REVIEW to decline");
        }

        User approver = userRepo.findById(approverUserId)
                .orElseThrow(() -> {
                    log.error("Approver not found → {}", approverUserId);
                    return new EntityNotFoundException("Approver user not found: " + approverUserId);
                });

        rr.setStatus(RequestStatus.DECLINED);
        rr.setApprovedBy(approver);
        rr.setDecisionAt(Instant.now());

        ResourceRequest saved = requestRepo.save(rr);

        log.info("Request declined → requestId={}", saved.getRequestId());

        return saved;
    }

    // =================================================
    // LISTS & FETCH
    // =================================================
    @Override
    @Transactional(readOnly = true)
    public List<ResourceRequest> listByStatus(RequestStatus status) {
        log.info("Listing requests by status={}", status);
        return requestRepo.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceRequest> listByRequester(Long requesterUserId) {
        log.info("Listing requests for requesterId={}", requesterUserId);

        User requester = userRepo.findById(requesterUserId)
                .orElseThrow(() -> {
                    log.error("Requester not found → {}", requesterUserId);
                    return new EntityNotFoundException("Requester not found: " + requesterUserId);
                });

        return requestRepo.findByRequester(requester);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceRequest getById(Long requestId) {
        log.info("Fetching Request by ID → {}", requestId);

        return requestRepo.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Request not found → {}", requestId);
                    return new EntityNotFoundException("ResourceRequest not found: " + requestId);
                });
    }

    // =================================================
    // MARK AS IN REVIEW
    // =================================================
    @Override
    public ResourceRequest markInReview(Long requestId, Long reviewerUserId) {

        log.info("Marking Request as IN_REVIEW → requestId={}, reviewerId={}", requestId, reviewerUserId);

        ResourceRequest rr = getById(requestId);

        if (rr.getStatus() == RequestStatus.IN_REVIEW ||
            rr.getStatus() == RequestStatus.APPROVED ||
            rr.getStatus() == RequestStatus.DECLINED) {

            log.warn("Skipping IN_REVIEW → request already in terminal/active state. status={}", rr.getStatus());
            return rr;
        }

        if (rr.getStatus() != RequestStatus.SUBMITTED) {
            log.error("Invalid IN_REVIEW transition → currentStatus={}", rr.getStatus());
            throw new IllegalStateException("Only SUBMITTED requests can be moved to IN_REVIEW");
        }

        User reviewer = userRepo.findById(reviewerUserId)
                .orElseThrow(() -> {
                    log.error("Reviewer not found → {}", reviewerUserId);
                    return new EntityNotFoundException("Reviewer user not found: " + reviewerUserId);
                });

        rr.setStatus(RequestStatus.IN_REVIEW);
        rr.setApprovedBy(reviewer);
        rr.setDecisionAt(Instant.now());

        ResourceRequest saved = requestRepo.save(rr);

        log.info("Request moved to IN_REVIEW → requestId={}", saved.getRequestId());

        return saved;
    }
}