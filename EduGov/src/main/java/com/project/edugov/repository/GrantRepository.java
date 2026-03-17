package com.project.edugov.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.Grant;

@Repository
public interface GrantRepository extends JpaRepository<Grant, Long> {

    // Check if a project is already funded
    Optional<Grant> findByProject_ProjectId(Long projectId);

    // List all grants associated with a faculty member's research
    List<Grant> findByFaculty_FacultyId(Long facultyId);
}
