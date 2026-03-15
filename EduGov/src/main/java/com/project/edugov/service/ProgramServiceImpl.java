package com.project.edugov.service;

import com.project.edugov.model.Program;
import com.project.edugov.model.User;
import com.project.edugov.repository.ProgramRepository;
import com.project.edugov.repository.UserRepository;
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
        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        program.setCreatedByAdmin(admin); // Link creator
        return programRepo.save(program);
    }

    @Override
    public List<Program> searchPrograms(String title) {
        return programRepo.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public Program getProgramById(Long id) {
        return programRepo.findById(id).orElseThrow(() -> new RuntimeException("Program not found"));
    }

    @Override
    public Program updateProgram(Long id, Program details) {
        Program existing = getProgramById(id);
        existing.setTitle(details.getTitle());
        existing.setDescription(details.getDescription());
        existing.setStartDate(details.getStartDate());
        existing.setEndDate(details.getEndDate());
        existing.setStatus(details.getStatus());
        return programRepo.save(existing); // user (admin_id) remains unchanged
    }
}