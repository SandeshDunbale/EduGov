package com.project.edugov.service;

import com.project.edugov.model.Program;
import java.util.List;

public interface ProgramService {
    Program createProgram(Program program, Long adminId);
    List<Program> searchPrograms(String title); // Half-spelling search
    Program updateProgram(Long id, Program program);
    Program getProgramById(Long id);
}