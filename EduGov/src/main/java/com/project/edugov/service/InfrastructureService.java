package com.project.edugov.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.edugov.model.Infrastructure;
import com.project.edugov.model.InfrastructureStatus;
import com.project.edugov.model.InfrastructureType;
import com.project.edugov.model.Program;

public interface InfrastructureService {

	Infrastructure create(Long programId, InfrastructureType type, String location, Integer capacity,
			InfrastructureStatus status);

	Infrastructure getById(Long infraId);

	List<Infrastructure> findByProgram(Program program);
	
	List<Infrastructure> findByProgramId(Long programId);

	Infrastructure updateStatus(Long infraId, InfrastructureStatus status);
	Infrastructure update(Long id, Long programId, InfrastructureType type, String location,
            Integer capacity, InfrastructureStatus status);

	Infrastructure markInUse(Long infraId);

	List<Infrastructure> findAll();
	
	void delete(Long infraId);

}