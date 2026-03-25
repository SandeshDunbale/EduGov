package com.project.edugov.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.edugov.dto.ProgramDTO;
import com.project.edugov.exception.APIException;
import com.project.edugov.model.Program;
import com.project.edugov.service.ProgramService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/programs")
@Slf4j
public class ProgramController {

	@Autowired
	private ProgramService programService;

	// Admin Create new program
	@PostMapping("/save")
	public ResponseEntity<ProgramDTO> createProgram(@Valid @RequestBody Program program) {
		Long adminId = (program.getCreatedByAdmin() != null) ? program.getCreatedByAdmin().getUserId() : null;
		if (adminId == null) {
			throw new APIException(HttpStatus.BAD_REQUEST, "Admin ID missing in request body.");
		}
		log.info("Request: Create program '{}' by Admin ID: {}", program.getTitle(), adminId);
		return ResponseEntity.ok(programService.createProgram(program, adminId));
	}

	// search program by title
	@GetMapping("/search/{title}")
	public ResponseEntity<List<ProgramDTO>> searchPrograms(@PathVariable String title) {
		log.info("Request: Search programs by title: {}", title);
		return ResponseEntity.ok(programService.searchPrograms(title));
	}

	// Get Program by ID
	@GetMapping("/{id}")
	public ResponseEntity<ProgramDTO> getProgramById(@PathVariable Long id) {
		log.info("Request: Fetch Program with ID: {}", id);
		return ResponseEntity.ok(programService.getProgramById(id));
	}

	// Update the program
	@PatchMapping("/update/{id}")
	public ResponseEntity<ProgramDTO> updateProgram(@RequestBody Program details, @PathVariable Long id) {
		log.info("Request: Update Program with ID: {}", id);
		return ResponseEntity.ok(programService.updateProgramById(id, details));
	}

	// Get all programs
	@GetMapping("/all")
	public ResponseEntity<List<ProgramDTO>> getAllPrograms() {
		log.info("Request: Fetching all programs");
		return ResponseEntity.ok(programService.getAllPrograms());
	}
}