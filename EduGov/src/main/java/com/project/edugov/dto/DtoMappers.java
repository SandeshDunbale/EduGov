package com.project.edugov.dto;

import com.project.edugov.model.Infrastructure;
import com.project.edugov.model.Resource;
import com.project.edugov.model.ResourceRequest;

public final class DtoMappers {

    private DtoMappers() {}


 // Resource
 public static ResourceResponse toDto(Resource r) {
     Long programId = (r.getProgram() != null) ? r.getProgram().getProgramID() : null;
     return new ResourceResponse(
             r.getResourceId(),
             programId,
             r.getType(),
             r.getQuantity(),
             r.getStatus()
     );
 }



//Infrastructure
public static InfrastructureResponse toDto(Infrastructure i) {
  Long programId = (i.getProgram() != null) ? i.getProgram().getProgramID() : null;
  return new InfrastructureResponse(
          i.getInfraId(),
          programId,
          i.getType(),
          i.getLocation(),
          i.getCapacity(),
          i.getStatus()
  );
}


    // ResourceRequest
    public static ResourceRequestResponse toDto(ResourceRequest rr) {
        Long resourceId = rr.getResource() != null ? rr.getResource().getResourceId() : null;
        Long infraId    = rr.getInfrastructure() != null ? rr.getInfrastructure().getInfraId() : null;
        Long approverId = rr.getApprovedBy() != null ? rr.getApprovedBy().getUserId() : null;

        return new ResourceRequestResponse(
                rr.getRequestId(),
                rr.getRequester().getUserId(),
                rr.getItemType(),
                resourceId,
                infraId,
                rr.getQuantity(),
                rr.getStatus(),
                approverId,
                rr.getCreatedAt(),
                rr.getUpdatedAt(),
                rr.getDecisionAt()
        );
    }
}