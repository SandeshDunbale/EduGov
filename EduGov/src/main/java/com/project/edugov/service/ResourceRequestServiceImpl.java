package com.project.edugov.service;
 
import java.time.Instant;
import java.util.List;
 
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.EntityNotFoundException;
 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import com.project.edugov.exception.RoleMismatchException;
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
    private final NotificationService notificationService;
    private final ResourceService resourceService;
    private final InfrastructureService infrastructureService;
 
    public ResourceRequestServiceImpl(ResourceRequestRepository requestRepo,
                                      ResourceRepository resourceRepo,
                                      InfrastructureRepository infraRepo,
                                      UserRepository userRepo,
                                      ResourceService resourceService,
                                      InfrastructureService infrastructureService,
                                      NotificationService notificationService) {
 
        this.requestRepo = requestRepo;
        this.resourceRepo = resourceRepo;
        this.infraRepo = infraRepo;
        this.userRepo = userRepo;
        this.resourceService = resourceService;
        this.infrastructureService = infrastructureService;
        this.notificationService = notificationService;
 
        log.info("ResourceRequestServiceImpl initialized");
    }
 
    // ----------------------------------------------------
    // ROLE VALIDATION LOGIC (STUDENT → RESOURCE, FACULTY → INFRA)
    // ----------------------------------------------------
    private void validateRole(User requester, RequestItemType type) {
 
        if (type == RequestItemType.RESOURCE && requester.getRole() != Role.STUDENT) {
            throw new RoleMismatchException("Only STUDENT can submit RESOURCE requests.");
        }
 
        if (type == RequestItemType.INFRASTRUCTURE && requester.getRole() != Role.FACULTY) {
            throw new RoleMismatchException("Only FACULTY can submit INFRASTRUCTURE requests.");
        }
    }
 
    // ----------------------------------------------------
    // SUBMIT RESOURCE REQUEST
    // ----------------------------------------------------
    @Override
    public ResourceRequest submitResourceRequest(Long requesterUserId, Long resourceId, int quantity) {
 
        log.info("Submitting Resource Request → requesterId={}, resourceId={}, qty={}",
                requesterUserId, resourceId, quantity);
 
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0.");
        }
 
        User requester = userRepo.findById(requesterUserId)
                .orElseThrow(() -> new EntityNotFoundException("Requester not found: " + requesterUserId));
 
        // ✅ Enforce STUDENT rule
        validateRole(requester, RequestItemType.RESOURCE);
 
        Resource resource = resourceRepo.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found: " + resourceId));
 
        ResourceRequest rr = ResourceRequest.builder()
                .requester(requester)
                .resource(resource)
                .itemType(RequestItemType.RESOURCE)
                .quantity(quantity)
                .status(RequestStatus.SUBMITTED)
                .build();
 
        ResourceRequest saved = requestRepo.save(rr);
 
        String message = requester.getName() + " submitted a RESOURCE request.";
        notifyProgramManagers(saved.getRequestId(), message);
 
        return saved;
    }
 
    // ----------------------------------------------------
    // SUBMIT INFRASTRUCTURE REQUEST
    // ----------------------------------------------------
    @Override
    public ResourceRequest submitInfrastructureRequest(Long requesterUserId, Long infraId) {
 
        log.info("Submitting Infrastructure Request → requesterId={}, infraId={}",
                requesterUserId, infraId);
 
        User requester = userRepo.findById(requesterUserId)
                .orElseThrow(() -> new EntityNotFoundException("Requester not found: " + requesterUserId));
 
        // ✅ Enforce FACULTY rule
        validateRole(requester, RequestItemType.INFRASTRUCTURE);
 
        Infrastructure infra = infraRepo.findById(infraId)
                .orElseThrow(() -> new EntityNotFoundException("Infrastructure not found: " + infraId));
 
        ResourceRequest rr = ResourceRequest.builder()
                .requester(requester)
                .infrastructure(infra)
                .itemType(RequestItemType.INFRASTRUCTURE)
                .status(RequestStatus.SUBMITTED)
                .build();
 
        ResourceRequest saved = requestRepo.save(rr);
 
        String message = requester.getName() + " submitted an INFRASTRUCTURE request.";
        notifyProgramManagers(saved.getRequestId(), message);
 
        return saved;
    }
 
    // ----------------------------------------------------
    // SEND NOTIFICATION TO PROGRAM MANAGERS
    // ----------------------------------------------------
    private void notifyProgramManagers(Long reqId, String message) {
        List<User> managers = userRepo.findByRole(Role.PROG_MANAGER);
 
        for (User pm : managers) {
            notificationService.createNotification(
                    pm.getUserId(),
                    reqId,
                    message,
                    "REQUEST",
                    pm.getEmail()
            );
        }
    }
 
    // ----------------------------------------------------
    // APPROVE REQUEST
    // ----------------------------------------------------
    @Override
    public ResourceRequest approve(Long requestId, Long approverUserId) {
 
        log.info("Approving Request → id={}, approver={}", requestId, approverUserId);
 
        ResourceRequest rr = getById(requestId);
 
        if (rr.getStatus() == RequestStatus.APPROVED || rr.getStatus() == RequestStatus.DECLINED) {
            return rr;
        }
 
        User approver = userRepo.findById(approverUserId)
                .orElseThrow(() -> new EntityNotFoundException("Approver not found: " + approverUserId));
 
        if (rr.getItemType() == RequestItemType.RESOURCE) {
            resourceService.allocate(rr.getResource().getResourceId(), rr.getQuantity());
        } else {
            infrastructureService.markInUse(rr.getInfrastructure().getInfraId());
        }
 
        rr.setStatus(RequestStatus.APPROVED);
        rr.setApprovedBy(approver);
        rr.setDecisionAt(Instant.now());
 
        ResourceRequest saved = requestRepo.save(rr);
 
        // ✅ Approval message with your custom text
        String msg = "Good news! Your request has been APPROVED.\n- Assessment submitted already";
 
        notificationService.createNotification(
                rr.getRequester().getUserId(),
                rr.getRequestId(),
                msg,
                "REQUEST",
                rr.getRequester().getEmail()
        );
 
        return saved;
    }
 
    // ----------------------------------------------------
    // DECLINE REQUEST
    // ----------------------------------------------------
    @Override
    public ResourceRequest decline(Long requestId, Long approverUserId, String reason) {
 
        log.warn("Declining Request → id={}, approver={}", requestId, approverUserId);
 
        ResourceRequest rr = getById(requestId);
 
        if (rr.getStatus() == RequestStatus.APPROVED || rr.getStatus() == RequestStatus.DECLINED) {
            return rr;
        }
 
        User approver = userRepo.findById(approverUserId)
                .orElseThrow(() -> new EntityNotFoundException("Approver not found"));
 
        rr.setStatus(RequestStatus.DECLINED);
        rr.setApprovedBy(approver);
        rr.setDecisionAt(Instant.now());
 
        ResourceRequest saved = requestRepo.save(rr);
 
        String msg = "Your request has been DECLINED.\n- Assessment submitted already";
 
        if (reason != null && !reason.isBlank()) {
            msg += " Reason: " + reason;
        }
 
        notificationService.createNotification(
                rr.getRequester().getUserId(),
                rr.getRequestId(),
                msg,
                "REQUEST",
                rr.getRequester().getEmail()
        );
 
        return saved;
    }
 
    // ----------------------------------------------------
    // LIST / GET
    // ----------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ResourceRequest> listByStatus(RequestStatus status) {
        return requestRepo.findByStatus(status);
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<ResourceRequest> listByRequester(Long requesterUserId) {
        User requester = userRepo.findById(requesterUserId)
                .orElseThrow(() -> new EntityNotFoundException("Requester not found"));
        return requestRepo.findByRequester(requester);
    }
 
    @Override
    @Transactional(readOnly = true)
    public ResourceRequest getById(Long requestId) {
        return requestRepo.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
    }
 
 
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