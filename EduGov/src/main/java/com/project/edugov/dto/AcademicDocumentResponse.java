package com.project.edugov.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicDocumentResponse {
    // These fields are what the user will see in Postman
    private String message;
    private Long documentId;
    private String docType;
    private String docNum;
    private String uploadStatus;
    private Instant uploadedAt;
}