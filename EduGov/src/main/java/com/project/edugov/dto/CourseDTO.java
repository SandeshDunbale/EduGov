package com.project.edugov.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.project.edugov.model.Status;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "adminId", "adminName", "facultyId", "facultyName", "facultyEmail", "programID", "programTitle",
		"programStatus", "courseID", "title", "description", "status" })
public class CourseDTO {
	// Admin Info
	private Long adminId;
	private String adminName;

	// Faculty Info
	private Long facultyId;
	private String facultyName;
	private String facultyEmail;

	// Program Info
	private Long programID;
	private String programTitle;
	private String programStatus;

	// Course Info
	private Long courseID;
	private String title;
	private String description;
	private Status status;
}