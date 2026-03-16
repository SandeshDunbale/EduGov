package com.project.edugov.service;

// src/com.project.edugov/service/impl/ResourceServiceImpl.java


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.edugov.model.Program;
import com.project.edugov.model.Resource;
import com.project.edugov.model.ResourceStatus;
import com.project.edugov.model.ResourceType;
import com.project.edugov.repository.ProgramRepository;
import com.project.edugov.repository.ResourceRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepo;
    private final ProgramRepository programRepo;

    public ResourceServiceImpl(ResourceRepository resourceRepo, ProgramRepository programRepo) {
        this.resourceRepo = resourceRepo;
        this.programRepo = programRepo;
    }

    @Override
    public Resource create(Long programId, ResourceType type, Integer quantity, ResourceStatus status) {
        Program p = programRepo.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + programId));
        Resource r = Resource.builder()
                .program(p)
                .type(type)
                .quantity(quantity)
                .status(status)
                .build();
        return resourceRepo.save(r);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getById(Long resourceId) {
        return resourceRepo.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found: " + resourceId));
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<Resource> findByProgram(Program program) {
        return resourceRepo.findByProgram(program);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Resource> findByStatus(ResourceStatus status, Pageable pageable) {
        return resourceRepo.findByStatus(status, pageable);
    }

    @Override
    public Resource updateStatus(Long resourceId, ResourceStatus status) {
        Resource r = getById(resourceId);
        r.setStatus(status);
        return resourceRepo.save(r);
    }

    @Override
    public Resource allocate(Long resourceId, int qtyToAllocate) {
        if (qtyToAllocate <= 0) {
            throw new IllegalArgumentException("qtyToAllocate must be > 0");
        }
        Resource r = getById(resourceId);
        if (r.getQuantity() != null) {
            int available = r.getQuantity();
            if (available < qtyToAllocate) {
                throw new IllegalStateException("Insufficient quantity. Available=" + available);
            }
            r.setQuantity(available - qtyToAllocate);
        }
        r.setStatus(ResourceStatus.ALLOCATED);
        return resourceRepo.save(r);
    }
}