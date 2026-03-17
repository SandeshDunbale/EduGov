package com.project.edugov.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.edugov.model.ResearchProject;
import com.project.edugov.service.ResearchProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ResearchProjectController {

    private final ResearchProjectService projectService;

    @PostMapping("/{facultyId}")
   // @PreAuthorize(hasRole="")   for only access to faculty
    public ResponseEntity<ResearchProject> createProject(
            @Valid @RequestBody ResearchProject project, 
            @PathVariable Long facultyId) {
        
        ResearchProject createdProject = projectService.createProject(project, facultyId);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<ResearchProject>> getProjectsByFaculty(@PathVariable Long facultyId) {
        return ResponseEntity.ok(projectService.getProjectsByFaculty(facultyId));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ResearchProject> getProjectById(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ResearchProject> updateProject(
            @PathVariable Long projectId, 
            @Valid @RequestBody ResearchProject projectDetails) {
        
        ResearchProject updatedProject = projectService.updateProject(projectId, projectDetails);
        return ResponseEntity.ok(updatedProject);
    }
}
