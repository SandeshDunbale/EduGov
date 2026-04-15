package com.project.edugov.dto;

import java.time.LocalDateTime;

import com.project.edugov.model.Status; // Import your existing Status Enum

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDTO {

	// 1. Admin Info
	private Long approvedByAdminId;
	private String approvedByAdminName;

	// 2. Faculty Info
	private Long facultyid;
	private String facultyname;

	// 3. Course Info
	private Long courseId;
	private String courseTitle;

	// 4. Enrollment Identity & Date
	private Long enrollmentID;
	private LocalDateTime enrollmentDate;

	// 5. THE FIX: Using your Status Enum
	private Status status;

	// 6. Student Info
	private String studentName;
	private String studentEmail;
}