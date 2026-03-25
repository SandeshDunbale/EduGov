package com.project.edugov.repository;

import com.project.edugov.model.Resource;
import com.project.edugov.model.Program;
import com.project.edugov.model.ResourceStatus;
import com.project.edugov.model.ResourceType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>, JpaSpecificationExecutor<Resource> {


    List<Resource> findByProgram(Program program);
    List<Resource> findByStatus(ResourceStatus status);
    List<Resource> findByType(ResourceType type);

    List<Resource> findByProgramAndStatus(Program program, ResourceStatus status);
    List<Resource> findByTypeAndStatus(ResourceType type, ResourceStatus status);




    Optional<Resource> findFirstByProgramAndTypeAndStatus(Program program, ResourceType type, ResourceStatus status);

    long countByProgramAndStatus(Program program, ResourceStatus status);
}