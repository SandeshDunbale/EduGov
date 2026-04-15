
package com.project.edugov.service;

import com.project.edugov.model.Program;
import com.project.edugov.model.Resource;
import com.project.edugov.model.ResourceStatus;
import com.project.edugov.model.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResourceService {

    Resource create(Long programId, ResourceType type, Integer quantity, ResourceStatus status);

    Resource getById(Long resourceId);

    List<Resource> findByProgram(Program program);

    List<Resource> findByStatus(ResourceStatus status);

    Resource updateStatus(Long resourceId, ResourceStatus status);
    Resource update(Long id, Long programId, ResourceType type, Integer qty, ResourceStatus status);

    Resource allocate(Long resourceId, int qtyToAllocate); // decrements quantity (if not null) and sets status to ALLOCATED
    
    List<Resource> findAll();
    
    void delete(Long resourceId);
}