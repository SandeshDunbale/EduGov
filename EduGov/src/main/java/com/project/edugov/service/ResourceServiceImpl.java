package com.project.edugov.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.edugov.model.Program;
import com.project.edugov.model.RequestStatus;
import com.project.edugov.model.Resource;
import com.project.edugov.model.ResourceStatus;
import com.project.edugov.model.ResourceType;
import com.project.edugov.repository.ProgramRepository;
import com.project.edugov.repository.ResourceRepository;
import com.project.edugov.repository.ResourceRequestRepository;

import jakarta.persistence.EntityNotFoundException;

@Slf4j
@Service
@Transactional
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepo;
    private final ProgramRepository programRepo;
    private final ResourceRequestRepository requestRepo;
    
    public ResourceServiceImpl(ResourceRepository resourceRepo, 
                               ProgramRepository programRepo,
                               ResourceRequestRepository requestRepo) {
        this.resourceRepo = resourceRepo;
        this.programRepo = programRepo;
        this.requestRepo = requestRepo;

        log.info("ResourceServiceImpl initialized");
    }

    @Override
    public Resource create(Long programId, ResourceType type, Integer quantity, ResourceStatus status) {
        log.info("Creating Resource → programId={}, type={}, quantity={}, status={}", 
                 programId, type, quantity, status);

        Program p = programRepo.findById(programId)
            .orElseThrow(() -> new EntityNotFoundException("Program not found: " + programId));

        Resource r = Resource.builder()
                .program(p)
                .type(type)
                .quantity(quantity)
                .status(status)
                .build();

        Resource saved = resourceRepo.save(r);
        log.debug("Resource created successfully → resourceId={}", saved.getResourceId());
        return saved;
    }

    @Override
    public Resource getById(Long resourceId) {
        log.info("Fetching Resource by ID → {}", resourceId);

        return resourceRepo.findById(resourceId)
                .orElseThrow(() -> {
                    log.error("Resource not found for id={}", resourceId);
                    return new EntityNotFoundException("Resource not found: " + resourceId);
                });
    }

    @Override
    public List<Resource> findByProgram(Program program) {
        log.info("Finding resources for programId={}", program.getProgramID());
        return resourceRepo.findByProgram(program);
    }

    @Override
    public List<Resource> findByStatus(ResourceStatus status) {
        log.info("Finding resources by status={}", status);
        return resourceRepo.findByStatus(status);
    }

    @Override
    public Resource updateStatus(Long resourceId, ResourceStatus status) {
        log.info("Updating Resource status → id={}, newStatus={}", resourceId, status);

        Resource r = getById(resourceId);
        r.setStatus(status);

        Resource updated = resourceRepo.save(r);
        log.debug("Status updated successfully → id={}, status={}", resourceId, updated.getStatus());
        return updated;
    }
    @Override
    public Resource update(Long id, Long programId, ResourceType type, Integer qty, ResourceStatus status) {

        log.info("Updating resource details → id={}, programId={}, type={}, qty={}, status={}",
                 id, programId, type, qty, status);

        Resource r = getById(id);

        Program p = programRepo.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + programId));

        r.setProgram(p);
        r.setType(type);
        r.setQuantity(qty);
        r.setStatus(status);

        Resource updated = resourceRepo.save(r);

        log.debug("Resource updated successfully → id={}", updated.getResourceId());
        return updated;
    }
    

    @Override
    public Resource allocate(Long resourceId, int qtyToAllocate) {
        log.info("Allocating from resource → id={}, qtyRequested={}", resourceId, qtyToAllocate);

        if (qtyToAllocate <= 0) {
            log.error("Invalid quantity allocation → {}", qtyToAllocate);
            throw new IllegalArgumentException("qtyToAllocate must be > 0");
        }

        Resource r = getById(resourceId);

        if (r.getQuantity() != null) {
            int available = r.getQuantity();
            log.debug("Available quantity for resource {} → {}", resourceId, available);

            if (available < qtyToAllocate) {
                log.error("Allocation failed → insufficient quantity. Available={}, Requested={}", 
                           available, qtyToAllocate);
                throw new IllegalStateException("Insufficient quantity. Available=" + available);
            }
            r.setQuantity(available - qtyToAllocate);
        }

        r.setStatus(ResourceStatus.ALLOCATED);

        Resource updated = resourceRepo.save(r);
        log.debug("Resource allocated successfully → id={}, remainingQty={}", 
                  resourceId, updated.getQuantity());
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> findAll() {
        log.info("Fetching ALL resources");
        return resourceRepo.findAll();
    }

    @Override
    public void delete(Long resourceId) {
        log.warn("Deleting Resource → id={}", resourceId);

        Resource r = resourceRepo.findById(resourceId)
                .orElseThrow(() -> {
                    log.error("Cannot delete → resource not found id={}", resourceId);
                    return new EntityNotFoundException("Resource not found: " + resourceId);
                });

        long active = requestRepo.countByResourceAndStatusIn(
                r,
                List.of(
                        RequestStatus.SUBMITTED,
                        RequestStatus.IN_REVIEW,
                        RequestStatus.APPROVED
                )
        );

        if (active > 0) {
            log.error("Deletion blocked → {} active requests found for resource {}", active, resourceId);
            throw new IllegalStateException(
                    "Cannot delete resource " + resourceId + " – " + active + " active requests exist");
        }

        try {
            resourceRepo.delete(r);
            log.info("Resource deleted successfully → id={}", resourceId);
        } catch (DataIntegrityViolationException dive) {
            log.error("FK constraint error while deleting resource {} → {}", 
                      resourceId, dive.getMessage());

            throw new IllegalStateException(
                    "Cannot delete resource " + resourceId + " due to related data",
                    dive
            );
        }
    }
}
