package com.project.edugov.dto;

import java.time.Instant;

import com.project.edugov.model.RequestItemType;
import com.project.edugov.model.RequestStatus;

public record ResourceRequestResponse(
        Long requestId,
        Long requesterUserId,
        RequestItemType itemType,
        Long resourceId,          // nullable when infra request
        Long infraId,             // nullable when resource request
        Integer quantity,         // null for infra request
        RequestStatus status,
        Long approvedByUserId,    // null until decision
        Instant createdAt,
        Instant updatedAt,
        Instant decisionAt
) {}