package com.project.edugov.controller;

import com.project.edugov.dto.AcademicDocumentResponse;
import com.project.edugov.model.AcademicDocument;
import com.project.edugov.model.Status;
import com.project.edugov.model.User;
import com.project.edugov.repository.UserRepository;
import com.project.edugov.service.AcademicDocumentService;
import com.project.edugov.service.AuditServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/academic-documents")
@RequiredArgsConstructor // Automatically creates the constructor for final fields
public class AcademicDocumentController {

    private final AcademicDocumentService documentService;
    private final UserRepository userRepo;
    private final AuditServiceImpl auditService;
    
   
    @PostMapping("/{userType}/{userId}/{docType}")
    public ResponseEntity<AcademicDocumentResponse> upload(
            @PathVariable String userType,
            @PathVariable Long userId,
            @PathVariable String docType,
            @RequestParam("file") MultipartFile file,
            @RequestParam("docNum") String docNum) {

        try {
            // Validation
            if (!userType.equalsIgnoreCase("students") && !userType.equalsIgnoreCase("faculty")) {
                throw new RuntimeException("Invalid path: Use /students/ or /faculty/");
            }

            // Fetch User
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            // Call the combined Service method
            AcademicDocument savedDoc = documentService.uploadAndSave(file, user, userType, docType, docNum);

            auditService.logAction("UPLOAD_ACADEMIC_DOCUMENT", "USER_ID_" + userId);

            return ResponseEntity.ok(AcademicDocumentResponse.builder()
                    .message("File Uploaded and Record Saved to MySQL Successfully!")
                    .docType(savedDoc.getDocType())
                    .uploadStatus("COMPLETED")
                    .uploadedAt(savedDoc.getUploadedDate())
                    .build());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    AcademicDocumentResponse.builder()
                            .message("Error: " + e.getMessage())
                            .uploadStatus("FAILED")
                            .build()
            );
        }
    }

    // API 2: LIST (Get all documents for a specific user)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AcademicDocument>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(documentService.getDocumentsByUser(userId));
    }

    // API 3: DOWNLOAD / VIEW (Opens the file in the browser)
    @GetMapping("/view/{documentId}")
    public ResponseEntity<Resource> viewDocument(@PathVariable Long documentId) {
        try {
            Path filePath = documentService.getFilePath(documentId);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF) 
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    
    
    
    
 // specific user verification
//    @PatchMapping("/user/{userId}/verify-all")
//    public ResponseEntity<List<AcademicDocument>> verifyAllByUser(
//            @PathVariable Long userId,
//            @RequestParam Status status,
//            @RequestParam(required = false) String notes,
//            @RequestParam Long adminId) {
//
//        User admin = userRepo.findById(adminId)
//                .orElseThrow(() -> new RuntimeException("Admin not found"));
//
//     
//        return ResponseEntity.ok(documentService.verifyAllByUserId(userId, status, notes, admin));
//    }

    // API 4: VERIFY (Admin updates the status)
    @PatchMapping("/{documentId}/verify")
    public ResponseEntity<AcademicDocument> verify(
            @PathVariable Long documentId,
            @RequestParam Status status,
            @RequestParam(required = false) String notes,
            @RequestParam Long adminId) {

        auditService.logAction("VERIFY_DOCUMENT_" + status.name(), "DOCUMENT_ID_" + documentId);

        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));

        AcademicDocument updatedDoc = documentService.verifyDocument(documentId, status, notes, admin);
        return ResponseEntity.ok(updatedDoc);
    }
}