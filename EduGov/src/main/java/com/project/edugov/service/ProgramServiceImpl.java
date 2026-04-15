package com.project.edugov.service;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.project.edugov.dto.ProgramDTO;
import com.project.edugov.exception.APIException;
import com.project.edugov.exception.ResourceNotFoundException;
import com.project.edugov.model.Program;
import com.project.edugov.model.Role;
import com.project.edugov.model.User;
import com.project.edugov.repository.ProgramRepository;
import com.project.edugov.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProgramServiceImpl implements ProgramService {

	@Autowired
	private ProgramRepository programRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private NotificationService notificationService;
	
	
	
	

	// Helper method
	private ProgramDTO mapToCustomDto(Program p) {
		ProgramDTO dto = modelMapper.map(p, ProgramDTO.class);
		if (p.getCreatedByAdmin() != null) {
			dto.setAdminId(p.getCreatedByAdmin().getUserId());
			dto.setName(p.getCreatedByAdmin().getName());
			dto.setEmail(p.getCreatedByAdmin().getEmail());
		}
		return dto;
	}

	@Override
	public ProgramDTO createProgram(Program p, Long aId) {
		log.info("Creating program: {}", p.getTitle());

		// Check Admin
		User u = userRepo.findById(aId)
				.orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + aId));

		if (!u.getRole().equals(Role.UNIV_ADMIN)) {
			throw new APIException(HttpStatus.FORBIDDEN, "Only University Admins can create programs.");
		}

		// 2. Check title
		if (programRepo.existsByTitleIgnoreCase(p.getTitle())) {
			throw new APIException(HttpStatus.BAD_REQUEST, "Program title already exists.");
		}

		p.setCreatedByAdmin(u);
		Program saved = programRepo.save(p);
		
		List<User> students = userRepo.findByRole(Role.STUDENT);
	    
	    // 4. Loop through the list and send a notification to each student
	    for (User student : students) {
	        notificationService.createNotification(
	                student.getUserId(),                            // userId (The student receiving the alert)
	                saved.getProgramID(),                                  // entityId (The ID of the newly created Program)
	                "New Program Alert: " + saved.getTitle() + " is now available!", // message
	                "ACADEMICS",                                    // category (Changed from SECURITY)
	                student.getEmail()                              // email
	        );
	    }
		return mapToCustomDto(saved);
	}

	@Override
	public List<ProgramDTO> searchPrograms(String title) {
		log.info("Searching programs for: {}", title);

		List<Program> list = programRepo.findByTitleContainingIgnoreCase(title);
		if (list.isEmpty())
			throw new ResourceNotFoundException("No programs match: " + title);
		return list.stream().map(this::mapToCustomDto).toList();
	}

	@Override
	public ProgramDTO getProgramById(Long id) {
		log.info("Fetching Program details for ID: {}", id);

		// Get program
		Program program = programRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + id));
		return mapToCustomDto(program);
	}

	@Override
	public ProgramDTO updateProgramById(Long id, Program details) {
		Program existing = programRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Program not found."));

		// Title checks
		if (details.getTitle() != null) {
			String newTitle = details.getTitle().trim();

			if (newTitle.isEmpty())
				throw new APIException(HttpStatus.BAD_REQUEST, "Title cannot be empty.");

			if (newTitle.equalsIgnoreCase(existing.getTitle()))
				throw new APIException(HttpStatus.BAD_REQUEST, "Title is already the same.");

			if (programRepo.existsByTitleIgnoreCase(newTitle))
				throw new APIException(HttpStatus.BAD_REQUEST, "Title already in use.");
		}

		// Date Validations
		LocalDate start = (details.getStartDate() != null) ? details.getStartDate() : existing.getStartDate();
		LocalDate end = (details.getEndDate() != null) ? details.getEndDate() : existing.getEndDate();

		if (details.getStartDate() != null && details.getStartDate().isBefore(LocalDate.now())) {
			throw new APIException(HttpStatus.BAD_REQUEST, "Start date must be today or future.");
		}

		if (start != null && end != null && end.isBefore(start)) {
			throw new APIException(HttpStatus.BAD_REQUEST, "End date must be after start date.");
		}

		// Status & Description
		if (details.getStatus() != null && details.getStatus().equals(existing.getStatus()))
			throw new APIException(HttpStatus.BAD_REQUEST, "Status is already " + existing.getStatus());

		if (details.getDescription() != null && details.getDescription().equals(existing.getDescription()))
			throw new APIException(HttpStatus.BAD_REQUEST, "Description is already the same.");

		modelMapper.getConfiguration().setSkipNullEnabled(true);
		modelMapper.map(details, existing);

		log.info("Saving updates for Program ID: {}", id);
		return mapToCustomDto(programRepo.save(existing));
	}

	@Override
	public List<ProgramDTO> getAllPrograms() {
		log.info("Fetching all program records from database");

		List<Program> programs = programRepo.findAll();

		if (programs.isEmpty()) {
			throw new ResourceNotFoundException("No programs found in the system.");
		}
		return programs.stream().map(this::mapToCustomDto).toList();
	}
}