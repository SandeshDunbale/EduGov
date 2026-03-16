// src/com.project.edugov/service/impl/InfrastructureServiceImpl.java
package com.project.edugov.service;

import com.project.edugov.model.Infrastructure;
import com.project.edugov.model.Program;
import com.project.edugov.model.InfrastructureStatus;
import com.project.edugov.model.InfrastructureType;
import com.project.edugov.repository.InfrastructureRepository;
import com.project.edugov.repository.ProgramRepository;
import com.project.edugov.service.InfrastructureService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InfrastructureServiceImpl implements InfrastructureService {

    private final InfrastructureRepository infraRepo;
    private final ProgramRepository programRepo;

    public InfrastructureServiceImpl(InfrastructureRepository infraRepo, ProgramRepository programRepo) {
        this.infraRepo = infraRepo;
        this.programRepo = programRepo;
    }

    @Override
    public Infrastructure create(Long programId, InfrastructureType type, String location, Integer capacity, InfrastructureStatus status) {
        Program p = programRepo.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + programId));
        Infrastructure i = Infrastructure.builder()
                .program(p)
                .type(type)
                .location(location)
                .capacity(capacity)
                .status(status)
                .build();
        return infraRepo.save(i);
    }

    @Override
    @Transactional(readOnly = true)
    public Infrastructure getById(Long infraId) {
        return infraRepo.findById(infraId)
                .orElseThrow(() -> new EntityNotFoundException("Infrastructure not found: " + infraId));
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<Infrastructure> findByProgram(Program program) {
        return infraRepo.findByProgram(program);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Infrastructure> findByProgram(Program program, Pageable pageable) {
        return infraRepo.findByProgram(program, pageable);
    }

    @Override
    public Infrastructure updateStatus(Long infraId, InfrastructureStatus status) {
        Infrastructure i = getById(infraId);
        i.setStatus(status);
        return infraRepo.save(i);
    }

    @Override
    public Infrastructure markInUse(Long infraId) {
        Infrastructure i = getById(infraId);
        i.setStatus(InfrastructureStatus.IN_USE);
        return infraRepo.save(i);
    }
}