package com.project.edugov.service;

import com.project.edugov.dto.StudentDTO;
import com.project.edugov.dto.StudentResponseDTO;
import com.project.edugov.model.Status; // FIXED IMPORT
import java.util.List;
import java.util.Optional;

public interface StudentService {

    // CREATE
    StudentResponseDTO registerStudent(StudentDTO dto);

   
    List<StudentResponseDTO> getStudentsByStatus(Status status);
    
    Optional<StudentResponseDTO> getStudentById(Long id);
    
    Optional<StudentResponseDTO> getStudentByBusinessId(String studentId);

    
    
    
    // UPDATE
    StudentResponseDTO updateStudent(Long id, StudentDTO dto);

    StudentResponseDTO approveStudent(Long id);

    StudentResponseDTO declineStudent(Long id);

    
    
    // DELETE
    void deleteStudent(Long id);
}