package com.project.edugov.dto;

import java.time.Instant;
import com.project.edugov.model.RequestStatus;

public record ResourceRequestResponse(
        Long requestId,
        Long requesterUserId,
        String requesterName,
        Long resourceId,
        Integer quantity,
        RequestStatus status,
        Long approvedByUserId,
        Instant createdAt,
        Instant updatedAt,
        Instant decisionAt
) {}