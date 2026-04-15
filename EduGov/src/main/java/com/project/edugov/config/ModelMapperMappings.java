package com.project.edugov.config;

import com.project.edugov.dto.*;
import com.project.edugov.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class ModelMapperMappings {

    private final ModelMapper mm;

    public ModelMapperMappings(ModelMapper mm) {
        this.mm = mm;
    }

    @PostConstruct
    public void setup() {

        // =================================================
        // Resource → ResourceResponse
        // =================================================
        mm.typeMap(Resource.class, ResourceResponse.class)
          .setConverter(ctx -> {
              Resource r = ctx.getSource();
              Long programId =
                      r.getProgram() != null ? r.getProgram().getProgramID() : null;

              return new ResourceResponse(
                      r.getResourceId(),
                      programId,
                      r.getType(),
                      r.getQuantity(),
                      r.getStatus()
              );
          });

        // =================================================
        // Infrastructure → InfrastructureResponse
        // =================================================
        mm.typeMap(Infrastructure.class, InfrastructureResponse.class)
          .setConverter(ctx -> {
              Infrastructure i = ctx.getSource();
              Long programId =
                      i.getProgram() != null ? i.getProgram().getProgramID() : null;

              return new InfrastructureResponse(
                      i.getInfraId(),
                      programId,
                      i.getType(),
                      i.getLocation(),
                      i.getCapacity(),
                      i.getStatus()
              );
          });

        // =================================================
        // ResourceRequest → ResourceRequestResponse (RESOURCE)
        // =================================================
        mm.typeMap(ResourceRequest.class, ResourceRequestResponse.class)
          .setConverter(ctx -> {
              ResourceRequest rr = ctx.getSource();
              User requester = rr.getRequester();

              return new ResourceRequestResponse(
                      rr.getRequestId(),
                      requester.getUserId(),
                      requester.getName(),
                      rr.getResource().getResourceId(),
                      rr.getQuantity(),
                      rr.getStatus(),
                      rr.getApprovedBy() != null
                              ? rr.getApprovedBy().getUserId()
                              : null,
                      rr.getCreatedAt(),
                      rr.getUpdatedAt(),
                      rr.getDecisionAt()
              );
          });

        // =================================================
        // ResourceRequest → InfrastructureRequestResponse (INFRA)
        // =================================================
        mm.typeMap(ResourceRequest.class, InfrastructureRequestResponse.class)
          .setConverter(ctx -> {
              ResourceRequest rr = ctx.getSource();
              User requester = rr.getRequester();
              Infrastructure infra = rr.getInfrastructure();

              return new InfrastructureRequestResponse(
                      rr.getRequestId(),
                      requester.getUserId(),
                      requester.getName(),
                      infra.getInfraId(),
                      infra.getCapacity(),
                      rr.getStatus(),
                      rr.getApprovedBy() != null
                              ? rr.getApprovedBy().getUserId()
                              : null,
                      rr.getCreatedAt(),
                      rr.getUpdatedAt(),
                      rr.getDecisionAt()
              );
          });
    }
}