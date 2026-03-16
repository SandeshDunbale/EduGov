package com.project.edugov.service;

import java.util.List;

import com.project.edugov.model.ProjectStatus;
import com.project.edugov.model.ResearchProject;


public interface ResearchProjectService {

    ResearchProject createProject(ResearchProject project, Long facultyId);

    List<ResearchProject> getProjectsByFaculty(Long facultyId);

    ResearchProject getProjectById(Long projectId);

    List<ResearchProject> getProjectsByStatus(ProjectStatus status);

    ResearchProject updateProjectStatus(Long projectId, ProjectStatus newStatus);

    ResearchProject updateProject(Long projectId, ResearchProject projectDetails);
}

