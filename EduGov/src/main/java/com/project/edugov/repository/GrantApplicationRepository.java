package com.project.edugov.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.GrantApplication;
import com.project.edugov.model.GrantApplicationStatus;



@Repository
public interface GrantApplicationRepository extends JpaRepository<GrantApplication, Long> {

    
    List<GrantApplication> findByStatus(GrantApplicationStatus status);

    Optional<GrantApplication> findByProject_ProjectId(Long projectId);

    List<GrantApplication> findByFaculty_FacultyId(Long facultyId);
}