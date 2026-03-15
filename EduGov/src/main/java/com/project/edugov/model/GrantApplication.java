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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
 
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "GrantApplication")
public class GrantApplication {
 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ApplicationId")
	private long applicationID;
 
	@NotNull(message = "Faculty is required")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "FacultyId",referencedColumnName = "facultyId")
	private Faculty faculty; // facultyid
 
	@NotNull(message = "Faculty is required")
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ProjectId",referencedColumnName = "ProjectId")
	private ResearchProject project; // projectid
 
	@NotNull(message = "SubmittedDate is required")
	@PastOrPresent(message = "SubmittedDate cannot be in the future")
	@Column(name = "SubmittedDate", nullable = false)
	private LocalDate submittedDate;
 
	@NotNull(message = "Status is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "Status", nullable = false, length = 32)
	private GrantApplicationStatus status=GrantApplicationStatus.UNDER_REVIEW;
}
