// src/com.project.edugov/service/ResourceRequestService.java
package com.project.edugov.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.edugov.model.RequestStatus;
import com.project.edugov.model.ResourceRequest;

public interface ResourceRequestService {

    // Submitters (Student/Faculty)
    ResourceRequest submitResourceRequest(Long requesterUserId, Long resourceId, int quantity);
    ResourceRequest submitInfrastructureRequest(Long requesterUserId, Long infraId);

    // Approvals (University Admin)
    ResourceRequest approve(Long requestId, Long approverUserId);
    ResourceRequest decline(Long requestId, Long approverUserId, String reasonOptional);

    // Queues
    Page<ResourceRequest> listByStatus(RequestStatus status, Pageable pageable);
    Page<ResourceRequest> listByRequester(Long requesterUserId, Pageable pageable);

    // Utilities
    ResourceRequest getById(Long requestId);
}