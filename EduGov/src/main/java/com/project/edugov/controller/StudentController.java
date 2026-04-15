package com.project.edugov.controller;

import com.project.edugov.dto.*;
import com.project.edugov.model.Status;
import com.project.edugov.service.AuditServiceImpl;
import com.project.edugov.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final AuditServiceImpl auditService;

    @PostMapping("/register")
    public ResponseEntity<StudentResponseDTO> register(@Valid @RequestBody StudentDTO dto) {
        auditService.logAction("REGISTER_STUDENT", "STUDENT_MODULE");
        return new ResponseEntity<>(studentService.registerStudent(dto), HttpStatus.CREATED);
    }

    // 
    //  /students?status=PENDING or /students?status=APPROVE
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getByStatus(@RequestParam Status status) {
        return ResponseEntity.ok(studentService.getStudentsByStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<StudentResponseDTO> approve(@PathVariable Long id) {
        auditService.logAction("APPROVE_STUDENT", "STUDENT_ID_" + id);
        return ResponseEntity.ok(studentService.approveStudent(id));
    }

    @PatchMapping("/{id}/decline")
    public ResponseEntity<StudentResponseDTO> decline(@PathVariable Long id) {
        auditService.logAction("DECLINE_STUDENT", "STUDENT_ID_" + id);
        return ResponseEntity.ok(studentService.declineStudent(id));
    }

    
    
    
    @PutMapping("/{id}/update")
    public ResponseEntity<StudentResponseDTO> update(@PathVariable Long id, @Valid @RequestBody StudentDTO dto) {
        auditService.logAction("UPDATE_STUDENT", "STUDENT_ID_" + id);
        return ResponseEntity.ok(studentService.updateStudent(id, dto));
    }

    
    
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        auditService.logAction("DELETE_STUDENT", "STUDENT_ID_" + id);
        return ResponseEntity.ok("Deleted successfully.");
    }
}