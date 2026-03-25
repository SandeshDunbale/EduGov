package com.project.edugov.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.edugov.model.*;
import com.project.edugov.repository.InfrastructureRepository;
import com.project.edugov.repository.ProgramRepository;
import com.project.edugov.repository.ResourceRequestRepository;

import jakarta.persistence.EntityNotFoundException;

@Slf4j
@Service
@Transactional
public class InfrastructureServiceImpl implements InfrastructureService {

    private final InfrastructureRepository infraRepo;
    private final ProgramRepository programRepo;
    private final ResourceRequestRepository requestRepo;

    public InfrastructureServiceImpl(
            InfrastructureRepository infraRepo,
            ProgramRepository programRepo,
            ResourceRequestRepository requestRepo) {

        this.infraRepo = infraRepo;
        this.programRepo = programRepo;
        this.requestRepo = requestRepo;

        log.info("InfrastructureServiceImpl initialized");
    }

    // ---------------------------------------------
    // CREATE
    // ---------------------------------------------
    @Override
    public Infrastructure create(Long programId, InfrastructureType type,
                                 String location, Integer capacity, InfrastructureStatus status) {

        log.info("Creating Infrastructure → programId={}, type={}, location={}",
                 programId, type, location);

        Program p = programRepo.findById(programId)
                .orElseThrow(() -> {
                    log.error("Program not found → {}", programId);
                    return new EntityNotFoundException("Program not found: " + programId);
                });

        Infrastructure infra = Infrastructure.builder()
                .program(p)
                .type(type)
                .location(location)
                .capacity(capacity)
                .status(status)
                .build();

        Infrastructure saved = infraRepo.save(infra);
        log.debug("Infrastructure created successfully → infraId={}", saved.getInfraId());

        return saved;
    }

    // ---------------------------------------------
    // GET BY ID
    // ---------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public Infrastructure getById(Long infraId) {

        log.info("Fetching Infrastructure by ID → {}", infraId);

        return infraRepo.findById(infraId)
                .orElseThrow(() -> {
                    log.error("Infrastructure not found → {}", infraId);
                    return new EntityNotFoundException("Infrastructure not found: " + infraId);
                });
    }

    // ---------------------------------------------
    // FIND BY PROGRAM
    // ---------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<Infrastructure> findByProgram(Program program) {
        log.info("Fetching Infrastructure for programId={}", program.getProgramID());
        return infraRepo.findByProgram(program);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Infrastructure> findByProgramId(Long programId) {
        log.info("Fetching Infrastructure by programId={}", programId);
        return infraRepo.findByProgram_ProgramID(programId);
    }

    // ---------------------------------------------
    // FIND ALL
    // ---------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<Infrastructure> findAll() {
        log.info("Fetching ALL Infrastructure entries");
        return infraRepo.findAll();
    }

    // ---------------------------------------------
    // UPDATE STATUS
    // ---------------------------------------------
    @Override
    public Infrastructure updateStatus(Long infraId, InfrastructureStatus status) {
        log.info("Updating Infrastructure status → infraId={}, newStatus={}", infraId, status);

        Infrastructure infra = getById(infraId);
        infra.setStatus(status);

        Infrastructure updated = infraRepo.save(infra);
        log.debug("Infrastructure status updated → infraId={}, status={}",
                  updated.getInfraId(), updated.getStatus());

        return updated;
    }
    
    @Override
    public Infrastructure update(Long id, Long programId, InfrastructureType type,
                                 String location, Integer capacity, InfrastructureStatus status) {

        log.info("Updating Infrastructure → id={}, programId={}, type={}, location={}, cap={}, status={}",
                 id, programId, type, location, capacity, status);

        Infrastructure infra = getById(id);

        Program p = programRepo.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + programId));

        infra.setProgram(p);
        infra.setType(type);
        infra.setLocation(location);
        infra.setCapacity(capacity);
        infra.setStatus(status);

        Infrastructure updated = infraRepo.save(infra);

        log.debug("Infrastructure updated → id={}", updated.getInfraId());
        return updated;
    }
    
    // ---------------------------------------------
    // MARK IN USE
    // ---------------------------------------------
    @Override
    public Infrastructure markInUse(Long infraId) {
        log.info("Marking Infrastructure IN_USE → id={}", infraId);

        Infrastructure infra = getById(infraId);
        infra.setStatus(InfrastructureStatus.IN_USE);

        Infrastructure updated = infraRepo.save(infra);
        log.debug("Infrastructure marked IN_USE → infraId={}", updated.getInfraId());

        return updated;
    }

    // ---------------------------------------------
    // DELETE
    // ---------------------------------------------
    @Override
    public void delete(Long infraId) {

        log.warn("Deleting Infrastructure → id={}", infraId);

        Infrastructure infra = infraRepo.findById(infraId)
                .orElseThrow(() -> {
                    log.error("Cannot delete → infrastructure {} not found", infraId);
                    return new EntityNotFoundException("Infrastructure not found: " + infraId);
                });

        long activeRequests = requestRepo.countByInfrastructureAndStatusIn(
                infra,
                List.of(
                        RequestStatus.SUBMITTED,
                        RequestStatus.IN_REVIEW,
                        RequestStatus.APPROVED
                )
        );

        if (activeRequests > 0) {
            log.error("Deletion blocked → {} active requests exist for infraId={}",
                      activeRequests, infraId);

            throw new IllegalStateException(
                    "Cannot delete infrastructure " + infraId +
                            " – " + activeRequests + " active requests exist"
            );
        }

        try {
            infraRepo.delete(infra);
            log.info("Infrastructure deleted successfully → id={}", infraId);

        } catch (DataIntegrityViolationException dive) {

            log.error("DataIntegrityViolationException while deleting infra {} → {}",
                      infraId, dive.getMessage());

            throw new IllegalStateException(
                    "Cannot delete infrastructure " + infraId + " due to related data",
                    dive
            );
        }
    }
}
