package com.project.edugov.service;

import com.project.edugov.model.AcademicDocument;
import com.project.edugov.model.Status;
import com.project.edugov.model.User;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface AcademicDocumentService {
    
 
    String storeFile(MultipartFile file, Long userId, String userType, String docType) throws IOException;

    
    List<AcademicDocument> getDocumentsByUser(Long userId);

   
    Path getFilePath(Long documentId);

   
    AcademicDocument verifyDocument(Long documentId, Status status, String notes, User admin);
    
    
    List<AcademicDocument> verifyAllByUserId(Long userId, Status status, String notes, User admin);
    
 
    AcademicDocument saveDocumentMetadata(AcademicDocument doc);

    AcademicDocument uploadAndSave(MultipartFile file, User user, String userType, String docType, String docNum) throws IOException;
}