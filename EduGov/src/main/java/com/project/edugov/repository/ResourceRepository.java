package com.project.edugov.repository;

import com.project.edugov.model.Resource;
import com.project.edugov.model.Program;
import com.project.edugov.model.ResourceStatus;
import com.project.edugov.model.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>, JpaSpecificationExecutor<Resource> {

    // Basic lookups
    List<Resource> findByProgram(Program program);
    List<Resource> findByStatus(ResourceStatus status);
    List<Resource> findByType(ResourceType type);

    // Combined filters
    List<Resource> findByProgramAndStatus(Program program, ResourceStatus status);
    List<Resource> findByTypeAndStatus(ResourceType type, ResourceStatus status);

    // Paging for admin grids
    Page<Resource> findByStatus(ResourceStatus status, Pageable pageable);

    // Optional single lookup for safety
    Optional<Resource> findFirstByProgramAndTypeAndStatus(Program program, ResourceType type, ResourceStatus status);

    // Example of a small aggregate (count)
    long countByProgramAndStatus(Program program, ResourceStatus status);
}