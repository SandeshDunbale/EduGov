package com.project.edugov.controller;

import com.project.edugov.dto.ProgramDTO;
import com.project.edugov.model.Program;
import com.project.edugov.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {

    @Autowired
    private ProgramService programService;

    @PostMapping
    public ResponseEntity<Program> createProgram(@RequestBody Program program, @RequestParam Long adminId) {
        return ResponseEntity.ok(programService.createProgram(program, adminId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Program>> searchPrograms(@RequestParam String title) {
        // This triggers your custom fuzzy search logic
        return ResponseEntity.ok(programService.searchPrograms(title));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Program> updateProgram(@PathVariable Long id, @RequestBody Program program) {
        return ResponseEntity.ok(programService.updateProgram(id, program));
    }
}