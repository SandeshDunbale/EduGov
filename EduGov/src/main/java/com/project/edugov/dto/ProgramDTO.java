package com.project.edugov.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.project.edugov.model.Status;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "adminId", "email", "name", "programID", "title", "description", "startDate", "endDate",
		"status" })
public class ProgramDTO {
	private Long programID;
	private String title;
	private String description;
	private LocalDate startDate;
	private LocalDate endDate;
	private Status status;

	// Extra fields for your specific response
	private Long adminId;
	private String name;
	private String email;
}