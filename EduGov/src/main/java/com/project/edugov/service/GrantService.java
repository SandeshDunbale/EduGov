package com.project.edugov.service;


import java.util.List;

import com.project.edugov.model.Grant;
import com.project.edugov.model.GrantApplication;
import com.project.edugov.model.GrantStatus;

public interface GrantService {

    
    GrantApplication applyForGrant(GrantApplication application, Long projectId, Long facultyId);

    Grant approveGrantApplication(Long applicationId, Long managerId, GrantStatus decision);

    List<GrantApplication> getPendingApplications();

    Grant getGrantByProjectId(Long projectId);

    List<GrantApplication> getApplicationHistoryByFaculty(Long facultyId);
}

