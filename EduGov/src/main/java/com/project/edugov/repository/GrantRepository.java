package com.project.edugov.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.project.edugov.model.Grant;
import java.util.Optional;

public interface GrantRepository extends JpaRepository<Grant, Long> {
    Optional<Grant> findByProjectProjectId(Long projectId);
}
