package com.project.edugov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.Program;
import com.project.edugov.model.Status;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
	boolean existsByTitleIgnoreCase(String title);

	List<Program> findByTitleContainingIgnoreCase(String title);
	List<Program> findByStatus(Status status);
	long countByStatus(Status status);
}
