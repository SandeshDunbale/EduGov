package com.project.edugov.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.edugov.model.ResearchProject;

public interface ResearchProjectRepository extends JpaRepository<ResearchProject, Long> {
}
