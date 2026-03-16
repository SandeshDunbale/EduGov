package com.project.edugov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.edugov.model.ComplianceRecord;


//This repository is used to read compliance records from the database.
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long>{
	
	//get compliance records for a specific entity type
	//example- PROGRAM, PROJECT, GRANT, ENROLLMENT
	List<ComplianceRecord> findByEntityType(String entityType);
	
	//get compliance records for a specific entity id 
	List<ComplianceRecord> findByEntityId(Long entityId);

	//get compliance records for a specific entity type and id
	//example- PROJECT with id=5
	List<ComplianceRecord> findByEntityTypeAndEntityId(String entityType, Long entityId);

	//get compliance records based on result
	//example- COMPLIANT or NON_COMPLIANT
	List<ComplianceRecord> findByResult(String result);

	//get compliance records for a specific entity type and result
	//example- NON_COMPLAINT grants
	List<ComplianceRecord> findByEntityTypeAndResult(String entityType , String result);

	//get compliance records for a specific entity id and result
	//example- check if project id=10 is compliant
	List<ComplianceRecord> findByEntityIdAndResult(Long entityId, String result);

	
}
