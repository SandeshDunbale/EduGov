package com.project.edugov.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.edugov.model.Faculty;
import com.project.edugov.model.ProjectStatus;
import com.project.edugov.model.ResearchProject;
import com.project.edugov.repository.FacultyRepository;
import com.project.edugov.repository.ResearchProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class ResearchProjectServiceImpl implements ResearchProjectService {

    private final ResearchProjectRepository projectRepository;
    private final FacultyRepository facultyRepository;

    @Override
    @Transactional
    public ResearchProject createProject(ResearchProject project, Long facultyId) {
       
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found with ID: " + facultyId));

       
        if (projectRepository.existsByTitleAndFaculty_FacultyId(project.getTitle(), facultyId)) {
            throw new RuntimeException("A project with this title already exists for this faculty.");
        }

       
        project.setFaculty(faculty);

      
        project.setStatus(ProjectStatus.DRAFT);
     
        return projectRepository.save(project);
    }

    @Override
    public List<ResearchProject> getProjectsByFaculty(Long facultyId) {
        List<ResearchProject> projects = projectRepository.findByFaculty_FacultyId(facultyId);
        
        // Check if the list is empty manually
        if (projects.isEmpty()) {
            throw new RuntimeException("No projects found for Faculty ID: " + facultyId);
        }
        
        return projects;
    }

    @Override
    public ResearchProject getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
    }

    @Override
    public List<ResearchProject> getProjectsByStatus(ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public ResearchProject updateProjectStatus(Long projectId, ProjectStatus newStatus) {
        ResearchProject project = getProjectById(projectId);
        project.setStatus(newStatus);
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    public ResearchProject updateProject(Long projectId, ResearchProject details) {
        // 1. Fetch the existing project
        ResearchProject existingProject = getProjectById(projectId);

        // 2. Update the fields
        existingProject.setTitle(details.getTitle());
        existingProject.setDescription(details.getDescription());
        existingProject.setStartDate(details.getStartDate());
        existingProject.setEndDate(details.getEndDate());

        // Note: We usually DON'T update Faculty or Status here 
        // because those are handled by separate business logic.
        existingProject.setStatus(ProjectStatus.DRAFT);
        return projectRepository.save(existingProject);
    }
}
