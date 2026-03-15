package com.project.edugov.model;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
 
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "ResearchProject")
public class ResearchProject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ProjectId")
	private long projectId;
 
	@Column(name = "Project_Title", nullable = false, length = 255)
	@NotBlank(message = "Project name is required")
	private String title;
 
	@Size(max = 4000)
	@Column(name = "Project_Desp", nullable = false, length = 4000)
	@NotBlank(message = "Project description is required")
	private String description;
 
	@NotNull(message = "Faculty is required")
	@ManyToOne(fetch = FetchType.LAZY,optional = false) // Do NOT load the Faculty immediately when loading
															// ResearchProject. Load it only when it is actually needed.
	@JoinColumn(name = "FacultyId",referencedColumnName = "facultyId")
	private Faculty faculty;
 
//condition for edate>=sdate
	@NotNull
	@Column(name = "StartDate", nullable = false)
	private LocalDate startDate;
 
	@NotNull
	@Column(name = "EndDate", nullable = false)
	private LocalDate endDate;
 
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "Status", nullable = false, length = 32)
	private ProjectStatus status = ProjectStatus.DRAFT;
}
