package com.project.edugov.repository;

import com.project.edugov.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {

    
    List<Program> findByTitleContainingIgnoreCase(String title);
}