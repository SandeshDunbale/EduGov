// src/com.project.edugov/service/InfrastructureService.java
package com.project.edugov.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.edugov.model.Infrastructure;
import com.project.edugov.model.InfrastructureStatus;
import com.project.edugov.model.InfrastructureType;
import com.project.edugov.model.Program;

public interface InfrastructureService {

    Infrastructure create(Long programId, InfrastructureType type, String location, Integer capacity, InfrastructureStatus status);

    Infrastructure getById(Long infraId);

    List<Infrastructure> findByProgram(Program program);

    Page<Infrastructure> findByProgram(Program program, Pageable pageable);

    Infrastructure updateStatus(Long infraId, InfrastructureStatus status);

    Infrastructure markInUse(Long infraId);
}