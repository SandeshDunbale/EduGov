package com.project.edugov.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.edugov.dto.GrantApplicationDTO;
import com.project.edugov.dto.GrantResponseDTO;
import com.project.edugov.model.Grant;
import com.project.edugov.model.GrantApplication;
import com.project.edugov.model.GrantApplicationStatus;
import com.project.edugov.model.GrantStatus;
import com.project.edugov.model.ProjectStatus;
import com.project.edugov.model.ResearchProject;
import com.project.edugov.model.User;
import com.project.edugov.repository.GrantApplicationRepository;
import com.project.edugov.repository.GrantRepository;
import com.project.edugov.repository.ResearchProjectRepository;
import com.project.edugov.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GrantServiceImpl implements GrantService {

    private final GrantApplicationRepository applicationRepository;
    private final GrantRepository grantRepository;
    private final ResearchProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper; // Injected for DTO conversion

    @Override
    @Transactional
    public GrantApplicationDTO applyForGrant(GrantApplication application, Long projectId, Long facultyId) {
        // Prevent duplicate applications
        applicationRepository.findByProject_ProjectId(projectId).ifPresent(a -> {
            throw new RuntimeException("An application already exists for this project.");
        });

        ResearchProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found."));

        application.setProject(project);
        application.setFaculty(project.getFaculty());
        application.setStatus(GrantApplicationStatus.SUBMITTED);
        application.setSubmittedDate(LocalDate.now());

        GrantApplication savedApp = applicationRepository.save(application);
        
        // Return clean DTO
        return modelMapper.map(savedApp, GrantApplicationDTO.class);
    }

    @Override
    @Transactional
    public GrantResponseDTO approveGrantApplication(Long applicationId, Long userId, GrantStatus decision) {
        GrantApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found."));
        
        User programManager = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User (Program Manager) not found."));

        if (decision == GrantStatus.APPROVED) {
            app.setStatus(GrantApplicationStatus.APPROVED);
            
            ResearchProject project = app.getProject();
            project.setStatus(ProjectStatus.COMPLETED);
            projectRepository.save(project);

            Grant grant = new Grant();
            grant.setProject(project);
            grant.setFaculty(project.getFaculty());
            grant.setAmount(app.getRequestedAmount());
            grant.setDate(LocalDate.now());
            grant.setStatus(GrantStatus.APPROVED);
            grant.setApprovedBy(programManager);

            applicationRepository.save(app);
            Grant savedGrant = grantRepository.save(grant);
            
            // Return clean DTO with Manager's name mapped
            return modelMapper.map(savedGrant, GrantResponseDTO.class);
        } else {
            throw new RuntimeException("Unsupported decision status: " + decision);
        }
    }

    @Override
    public List<GrantApplicationDTO> getPendingApplications() {
        return applicationRepository.findByStatus(GrantApplicationStatus.SUBMITTED)
                .stream()
                .map(app -> modelMapper.map(app, GrantApplicationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public GrantResponseDTO getGrantByProjectId(Long projectId) {
        Grant grant = grantRepository.findByProject_ProjectId(projectId)
                .orElseThrow(() -> new RuntimeException("No grant found for this project."));
        
        return modelMapper.map(grant, GrantResponseDTO.class);
    }

    @Override
    public List<GrantApplicationDTO> getApplicationHistoryByFaculty(Long facultyId) {
        return applicationRepository.findByFaculty_FacultyId(facultyId)
                .stream()
                .map(app -> modelMapper.map(app, GrantApplicationDTO.class))
                .collect(Collectors.toList());
    }
}