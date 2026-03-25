package com.project.edugov.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.Infrastructure;
import com.project.edugov.model.RequestItemType;
import com.project.edugov.model.RequestStatus;
import com.project.edugov.model.Resource;
import com.project.edugov.model.ResourceRequest;
import com.project.edugov.model.User;

@Repository
public interface ResourceRequestRepository extends JpaRepository<ResourceRequest, Long>, JpaSpecificationExecutor<ResourceRequest> {


    List<ResourceRequest> findByStatusOrderByCreatedAtAsc(RequestStatus status);
    List<ResourceRequest> findByStatus(RequestStatus status);

    List<ResourceRequest> findByRequester(User requester);
    List<ResourceRequest> findByRequesterAndStatus(User requester, RequestStatus status);

    List<ResourceRequest> findByItemType(RequestItemType itemType);
    List<ResourceRequest> findByItemTypeAndStatus(RequestItemType itemType, RequestStatus status);

    List<ResourceRequest> findByResource(Resource resource);
    List<ResourceRequest> findByResourceAndStatus(Resource resource, RequestStatus status);

    List<ResourceRequest> findByInfrastructure(Infrastructure infrastructure);
    List<ResourceRequest> findByInfrastructureAndStatus(Infrastructure infrastructure, RequestStatus status);

    List<ResourceRequest> findByCreatedAtBetween(Instant from, Instant to);
    List<ResourceRequest> findByDecisionAtBetween(Instant from, Instant to);

    long countByStatus(RequestStatus status);
    long countByRequesterAndStatus(User requester, RequestStatus status);
    
    long countByResourceAndStatusIn(Resource resource, Collection<RequestStatus> statuses);
    long countByInfrastructureAndStatusIn(Infrastructure infrastructure, Collection<RequestStatus> statuses);
}
