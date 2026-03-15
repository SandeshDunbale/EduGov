package com.project.edugov.repository;

import com.project.edugov.model.*;
import com.project.edugov.model.RequestItemType;
import com.project.edugov.model.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ResourceRequestRepository extends JpaRepository<ResourceRequest, Long>, JpaSpecificationExecutor<ResourceRequest> {

    // Queues for University Admin
    List<ResourceRequest> findByStatusOrderByCreatedAtAsc(RequestStatus status);
    Page<ResourceRequest> findByStatus(RequestStatus status, Pageable pageable);

    // Requester-specific (Student/Faculty user)
    Page<ResourceRequest> findByRequester(User requester, Pageable pageable);
    List<ResourceRequest> findByRequesterAndStatus(User requester, RequestStatus status);

    // Polymorphic target filters
    List<ResourceRequest> findByItemType(RequestItemType itemType);
    Page<ResourceRequest> findByItemTypeAndStatus(RequestItemType itemType, RequestStatus status, Pageable pageable);

    // For resource approvals (quantity > 0 in your model guard)
    List<ResourceRequest> findByResource(Resource resource);
    List<ResourceRequest> findByResourceAndStatus(Resource resource, RequestStatus status);

    // For infrastructure approvals
    List<ResourceRequest> findByInfrastructure(Infrastructure infrastructure);
    List<ResourceRequest> findByInfrastructureAndStatus(Infrastructure infrastructure, RequestStatus status);

    // Date-range (dashboards, reporting)
    List<ResourceRequest> findByCreatedAtBetween(Instant from, Instant to);
    List<ResourceRequest> findByDecisionAtBetween(Instant from, Instant to);

    // Fast count for KPIs
    long countByStatus(RequestStatus status);
    long countByRequesterAndStatus(User requester, RequestStatus status);
}
