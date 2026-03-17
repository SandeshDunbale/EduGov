package com.project.edugov.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.GrantApplication;
import com.project.edugov.model.GrantApplicationStatus;

@Repository
public interface GrantApplicationRepository extends JpaRepository<GrantApplication, Long> {

    // For the Program Manager to see all new/pending applications
    List<GrantApplication> findByStatus(GrantApplicationStatus status);

    // To prevent a project from having multiple applications (1:1 constraint check)
    Optional<GrantApplication> findByProject_ProjectId(Long projectId);

    // For Faculty to track their specific applications
    List<GrantApplication> findByFaculty_FacultyId(Long facultyId);
}