package com.project.edugov.repository;

import com.project.edugov.model.Infrastructure;
import com.project.edugov.model.Program;
import com.project.edugov.model.InfrastructureStatus;
import com.project.edugov.model.InfrastructureType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfrastructureRepository extends JpaRepository<Infrastructure, Long>, JpaSpecificationExecutor<Infrastructure> {

    // Basic lookups
    List<Infrastructure> findByProgram(Program program);
    List<Infrastructure> findByStatus(InfrastructureStatus status);
    List<Infrastructure> findByType(InfrastructureType type);

    // Combined filters
    List<Infrastructure> findByProgramAndType(Program program, InfrastructureType type);
    List<Infrastructure> findByProgramAndStatus(Program program, InfrastructureStatus status);

    // Paging for admin view
    Page<Infrastructure> findByProgram(Program program, Pageable pageable);

    // Capacity‑based filters (handy for room selection UIs)
    List<Infrastructure> findByTypeAndCapacityGreaterThanEqual(InfrastructureType type, Integer minCapacity);
}