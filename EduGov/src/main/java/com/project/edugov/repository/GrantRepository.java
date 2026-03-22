package com.project.edugov.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.edugov.model.Grant;

public interface GrantRepository extends JpaRepository<Grant, Long> {
    Optional<Grant> findByProjectProjectId(Long projectId);
}
