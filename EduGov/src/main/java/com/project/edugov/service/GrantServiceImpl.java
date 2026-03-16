package com.project.edugov.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public GrantApplication applyForGrant(GrantApplication application, Long projectId, Long facultyId) {
      
        applicationRepository.findByProject_ProjectId(projectId).ifPresent(a -> {
            throw new RuntimeException("An application already exists for this project.");
        });

        ResearchProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found."));

        application.setProject(project);
        application.setFaculty(project.getFaculty());
        application.setStatus(GrantApplicationStatus.SUBMITTED);
        application.setSubmittedDate(LocalDate.now());

        return applicationRepository.save(application);
    }

    @Override
    @Transactional
    public Grant approveGrantApplication(Long applicationId, Long userId, GrantStatus decision) {
        // 1. Find the application and the User (Program Manager)
        GrantApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found."));
        
        User programManager = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User (Program Manager) not found."));

        switch (decision) {
        case APPROVED:
            app.setStatus(GrantApplicationStatus.APPROVED);
            
            // Upgrade Project Status
            ResearchProject project = app.getProject();
            project.setStatus(ProjectStatus.COMPLETED);
            projectRepository.save(project);

            // Create the Grant Record
            Grant grant = new Grant();
            grant.setProject(project);
            grant.setFaculty(project.getFaculty());
            grant.setAmount(app.getRequestedAmount());
            grant.setDate(LocalDate.now());
            grant.setStatus(GrantStatus.APPROVED);
            grant.setApprovedBy(programManager);

            applicationRepository.save(app);
            return grantRepository.save(grant);


        default:
            throw new RuntimeException("Unsupported decision status: " + decision);
    }
    }

    @Override
    public List<GrantApplication> getPendingApplications() {
        return applicationRepository.findByStatus(GrantApplicationStatus.SUBMITTED);
    }

    @Override
    public Grant getGrantByProjectId(Long projectId) {
        return grantRepository.findByProject_ProjectId(projectId)
                .orElseThrow(() -> new RuntimeException("No grant found for this project."));
    }

    @Override
    public List<GrantApplication> getApplicationHistoryByFaculty(Long facultyId) {
        return applicationRepository.findByFaculty_FacultyId(facultyId);
    }
}

