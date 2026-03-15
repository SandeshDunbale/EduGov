package com.project.edugov.service;

import com.project.edugov.model.Program;
import com.project.edugov.model.User;
import com.project.edugov.repository.ProgramRepository;
import com.project.edugov.repository.UserRepository;
import com.project.edugov.exception.ResourceNotFoundException; // Added
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProgramServiceImpl implements ProgramService {

    @Autowired
    private ProgramRepository programRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public Program createProgram(Program program, Long adminId) {
        // API: POST /api/programs
        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + adminId));
        
        program.setCreatedByAdmin(admin);
        return programRepo.save(program);
    }

    @Override
    public List<Program> searchPrograms(String title) {
        // API: GET /api/programs/search
        List<Program> list = programRepo.findByTitleContainingIgnoreCase(title);
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("No programs found matching the title: " + title);
        }
        return list;
    }

    @Override
    public Program getProgramById(Long id) {
        return programRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + id));
    }

    @Override
    public Program updateProgram(Long id, Program details) {
        // API: PUT /api/programs/{id}
        Program existing = programRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Update failed: Program ID " + id + " not found"));

        existing.setTitle(details.getTitle());
        existing.setDescription(details.getDescription());
        existing.setStartDate(details.getStartDate());
        existing.setEndDate(details.getEndDate());
        existing.setStatus(details.getStatus());
        
        return programRepo.save(existing);
    }
}