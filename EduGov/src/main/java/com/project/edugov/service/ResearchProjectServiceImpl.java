package com.project.edugov.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.edugov.dto.ProjectUpdateResponseDTO;
import com.project.edugov.dto.ResearchProjectDTO;
import com.project.edugov.exception.ResourceNotFoundException;
import com.project.edugov.model.Faculty;
import com.project.edugov.model.ProjectStatus;
import com.project.edugov.model.ResearchProject;
import com.project.edugov.repository.FacultyRepository;
import com.project.edugov.repository.ResearchProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Automatically injects the repositories
public class ResearchProjectServiceImpl implements ResearchProjectService {

    private final ResearchProjectRepository projectRepository;
    private final FacultyRepository facultyRepository;
    private final org.modelmapper.ModelMapper modelMapper;

    @Override
    @Transactional
    public ResearchProjectDTO createProject(ResearchProject project, Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with ID: " + facultyId));

        if (projectRepository.existsByTitleAndFaculty_FacultyId(project.getTitle(), facultyId)) {
            throw new RuntimeException("A project with this title already exists for this faculty.");
        }

        project.setFaculty(faculty);
        project.setStatus(ProjectStatus.DRAFT);
        
        ResearchProject savedProject = projectRepository.save(project);

        // Map the saved Entity to the DTO
        return modelMapper.map(savedProject, ResearchProjectDTO.class);
    }

    @Override
    public List<ResearchProjectDTO> getProjectsByFaculty(Long facultyId) {
        List<ResearchProject> projects = projectRepository.findByFaculty_FacultyId(facultyId);
        
        if (projects.isEmpty()) {
            throw new RuntimeException("No projects found for Faculty ID: " + facultyId);
        }

        // Map each Entity in the list to a DTO
        return projects.stream()
                .map(project -> modelMapper.map(project, ResearchProjectDTO.class))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public ResearchProjectDTO getProjectById(Long projectId) {
        // 1. Fetch the Entity from the DB
        ResearchProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
        
        // 2. Map the Entity to the DTO and return it
        return modelMapper.map(project, ResearchProjectDTO.class);
    }

    @Override
    public List<ResearchProject> getProjectsByStatus(ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public ResearchProject updateProjectStatus(Long projectId, ProjectStatus newStatus) {
        // 1. Fetch the ENTITY directly from the repository
        ResearchProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // 2. Perform the update
        project.setStatus(newStatus);

        // 3. Save and return the ENTITY
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    public ProjectUpdateResponseDTO updateProject(Long projectId, ResearchProject details) {
        // 1. Fetch the ENTITY directly from the repository (Fixes the Type Mismatch)
        ResearchProject existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        // 2. Update the fields
        existingProject.setTitle(details.getTitle());
        existingProject.setDescription(details.getDescription());
        existingProject.setStartDate(details.getStartDate());
        existingProject.setEndDate(details.getEndDate());

        // 3. Keep the status as DRAFT as per your logic
        existingProject.setStatus(ProjectStatus.DRAFT);

        // 4. Save the Entity
        ResearchProject updated = projectRepository.save(existingProject);

        // 5. Convert the saved Entity to the Update DTO (This hides dates and status from the response)
        return modelMapper.map(updated, ProjectUpdateResponseDTO.class);
    }
}
