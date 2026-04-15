package com.project.edugov.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.edugov.dto.GrantApplicationDTO;
import com.project.edugov.dto.GrantResponseDTO;
import com.project.edugov.exception.ResourceNotFoundException;
import com.project.edugov.model.Grant;
import com.project.edugov.model.GrantApplication;
import com.project.edugov.model.GrantApplicationStatus;
import com.project.edugov.model.GrantStatus;
import com.project.edugov.model.ProjectStatus;
import com.project.edugov.model.ResearchProject;
import com.project.edugov.model.Role;
import com.project.edugov.model.User;
import com.project.edugov.repository.GrantApplicationRepository;
import com.project.edugov.repository.GrantRepository;
import com.project.edugov.repository.ResearchProjectRepository;
import com.project.edugov.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrantServiceImpl implements GrantService {

	private final GrantApplicationRepository applicationRepository;
	private final GrantRepository grantRepository;
	private final ResearchProjectRepository projectRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final NotificationService notificationService;

	@Override
	@Transactional
	public GrantApplicationDTO applyForGrant(GrantApplication newDetails, Long projectId, Long facultyId) {
		log.info("Received grant application request for Project ID: {} from Faculty ID: {}", projectId, facultyId);

		ResearchProject project = projectRepository.findById(projectId).orElseThrow(() -> {
			log.error("Grant application failed: Project ID {} not found", projectId);
			return new ResourceNotFoundException("Project not found with ID: " + projectId);
		});

		Optional<GrantApplication> existingAppOpt = applicationRepository.findByProject_ProjectId(projectId);
		
		GrantApplication finalApp; // Store the saved app here to avoid duplicating the notification code

		if (existingAppOpt.isPresent()) {
			GrantApplication existingApp = existingAppOpt.get();

			if (existingApp.getStatus() != GrantApplicationStatus.REJECTED) {
				log.warn(
						"Duplicate application blocked. Project ID {} already has an active application with status: {}",
						projectId, existingApp.getStatus());
				throw new RuntimeException("An active application already exists for this project (Status: "
						+ existingApp.getStatus() + ")");
			}

			log.info("Re-applying for rejected project. Reusing Application ID: {}", existingApp.getApplicationID());
			existingApp.setRequestedAmount(newDetails.getRequestedAmount());
			existingApp.setStatus(GrantApplicationStatus.SUBMITTED);
			existingApp.setSubmittedDate(LocalDate.now());

			finalApp = applicationRepository.save(existingApp);
		} else {
			log.debug("Creating brand new grant application for Project ID: {}", projectId);
			newDetails.setProject(project);
			newDetails.setFaculty(project.getFaculty());
			newDetails.setStatus(GrantApplicationStatus.SUBMITTED);
			newDetails.setSubmittedDate(LocalDate.now());

			finalApp = applicationRepository.save(newDetails);
			log.info("Successfully saved new Grant Application with ID: {}", finalApp.getApplicationID());
		}

		// --- NEW NOTIFICATION LOGIC ---
		// Fetch all Program Managers
		List<User> programManagers = userRepository.findByRole(Role.PROG_MANAGER);
		
		// Loop through and notify each Program Manager
		for (User pm : programManagers) {
			notificationService.createNotification(
					pm.getUserId(),                            // userId (The Program Manager receiving the alert)
					finalApp.getApplicationID(),               // entityId (The ID of the grant application)
					"New Grant Application submitted for Project: " + project.getTitle(), // message
					"GRANTS",                                  // category
					pm.getEmail()                              // email
			);
		}
		// ------------------------------

		return modelMapper.map(finalApp, GrantApplicationDTO.class);
	}

	@Override
	@Transactional
	public GrantResponseDTO approveGrantApplication(Long applicationId, Long userId, GrantStatus decision) {
		log.info("Manager (User ID: {}) is making a decision [{}] for Application ID: {}", userId, decision,
				applicationId);

		GrantApplication app = applicationRepository.findById(applicationId).orElseThrow(() -> {
			log.error("Decision failed: Application ID {} not found", applicationId);
			return new ResourceNotFoundException("Application not found with ID: " + applicationId);
		});

		User programManager = userRepository.findById(userId).orElseThrow(() -> {
			log.error("Decision failed: Program Manager ID {} not found", userId);
			return new ResourceNotFoundException("Manager not found with ID: " + userId);
		});

		ResearchProject project = app.getProject();

		if (decision == GrantStatus.UNDER_REVIEW) {
			log.info("Moving Application {} and Project {} to UNDER_REVIEW", applicationId, project.getProjectId());
			app.setStatus(GrantApplicationStatus.UNDER_REVIEW);
			project.setStatus(ProjectStatus.UNDER_REVIEW);

			projectRepository.save(project);
			applicationRepository.save(app);
			return modelMapper.map(app, GrantResponseDTO.class);
		}

		else if (decision == GrantStatus.APPROVED) {
			log.info("Approving Grant for Application ID: {}. Finalizing project.", applicationId);
			app.setStatus(GrantApplicationStatus.APPROVED);
			project.setStatus(ProjectStatus.COMPLETED);
			projectRepository.save(project);

			Grant grant = grantRepository.findByProject_ProjectId(project.getProjectId()).orElse(new Grant());

			grant.setProject(project);
			grant.setFaculty(project.getFaculty());
			grant.setAmount(app.getRequestedAmount());
			grant.setDate(LocalDate.now());
			grant.setStatus(GrantStatus.APPROVED);
			grant.setApprovedBy(programManager);

			applicationRepository.save(app);
			Grant savedGrant = grantRepository.save(grant);
			log.info("Grant record finalized with ID: {} for Project: {}", savedGrant.getGrantId(), project.getTitle());

			// --- NEW NOTIFICATION LOGIC (APPROVED) ---
			if (project.getFaculty() != null && project.getFaculty().getUser() != null) {
				User facultyUser = project.getFaculty().getUser();
				notificationService.createNotification(
						facultyUser.getUserId(),
						app.getApplicationID(), 
						"Congratulations! Your grant application for '" + project.getTitle() + "' has been APPROVED.",
						"GRANTS",
						facultyUser.getEmail()
				);
			}
			// -----------------------------------------

			return modelMapper.map(savedGrant, GrantResponseDTO.class);
		}

		else if (decision == GrantStatus.REJECTED) {
			log.warn("Application ID: {} was REJECTED. Unlocking Project ID: {} for updates.", applicationId,
					project.getProjectId());
			app.setStatus(GrantApplicationStatus.REJECTED);
			project.setStatus(ProjectStatus.DRAFT);

			projectRepository.save(project);
			applicationRepository.save(app);

			// --- NEW NOTIFICATION LOGIC (REJECTED) ---
			if (project.getFaculty() != null && project.getFaculty().getUser() != null) {
				User facultyUser = project.getFaculty().getUser();
				notificationService.createNotification(
						facultyUser.getUserId(),
						app.getApplicationID(),
						"Your grant application for '" + project.getTitle() + "' has been REJECTED. The project has been unlocked for updates.",
						"GRANTS",
						facultyUser.getEmail()
				);
			}
			// -----------------------------------------

			GrantResponseDTO response = modelMapper.map(app, GrantResponseDTO.class);

			// Manually set the role from the PM you already fetched
			response.setApprovedByRole(programManager.getRole().toString());

			// Return the updated DTO
			return response;
		}

		else {
			log.error("Invalid decision status received: {}", decision);
			throw new RuntimeException("Unsupported decision status: " + decision);
		}
	}
	
	@Override
	public List<GrantApplicationDTO> getPendingApplications() {
		log.debug("Fetching all pending grant applications");
		return applicationRepository.findByStatus(GrantApplicationStatus.SUBMITTED).stream()
				.map(app -> modelMapper.map(app, GrantApplicationDTO.class)).collect(Collectors.toList());
	}

	@Override
	public GrantResponseDTO getGrantByProjectId(Long projectId) {
		log.debug("Retrieving grant details for Project ID: {}", projectId);
		Grant grant = grantRepository.findByProject_ProjectId(projectId).orElseThrow(() -> {
			log.warn("No grant record found for Project ID: {}", projectId);
			return new RuntimeException("No grant found for this project.");
		});

		return modelMapper.map(grant, GrantResponseDTO.class);
	}

	@Override
	public List<GrantApplicationDTO> getApplicationHistoryByFaculty(Long facultyId) {
		log.debug("Retrieving application history for Faculty ID: {}", facultyId);
		return applicationRepository.findByFaculty_FacultyId(facultyId).stream()
				.map(app -> modelMapper.map(app, GrantApplicationDTO.class)).collect(Collectors.toList());
	}
}
