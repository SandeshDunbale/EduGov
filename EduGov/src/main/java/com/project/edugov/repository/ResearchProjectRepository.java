package com.project.edugov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.ProjectStatus;
import com.project.edugov.model.ResearchProject;



@Repository
public interface ResearchProjectRepository extends JpaRepository<ResearchProject, Long> {

  
    List<ResearchProject> findByFaculty_FacultyId(Long facultyId);

    
    List<ResearchProject> findByStatus(ProjectStatus status);

    boolean existsByTitleAndFaculty_FacultyId(String title, Long facultyId);
}


