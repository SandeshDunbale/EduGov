package com.project.edugov.service;

import com.project.edugov.dto.FacultyDTO;
import com.project.edugov.dto.FacultyResponseDTO;
import com.project.edugov.model.Status;
import java.util.List;
import java.util.Optional;




public interface FacultyService {
	
	
    FacultyResponseDTO registerFaculty(FacultyDTO dto);
    List<FacultyResponseDTO> getFacultyByStatus(Status status);
    Optional<FacultyResponseDTO> getFacultyById(Long id);
    FacultyResponseDTO updateFaculty(Long id, FacultyDTO dto);
    FacultyResponseDTO approveFaculty(Long id);
    FacultyResponseDTO declineFaculty(Long id);
    void deleteFaculty(Long id);
}