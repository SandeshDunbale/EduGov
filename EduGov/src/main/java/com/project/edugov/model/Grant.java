package com.project.edugov.model;

import java.math.BigDecimal;
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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
 
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "grants")
public class Grant {
 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "GrantID", nullable = false, updatable = false)
	private long grantId;
 
	@NotNull(message = "Project is required")
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ProjectId")
	private ResearchProject project;
	/*
	 * @JoinColumn( name = "FacultyID", nullable = false, foreignKey
	 * = @ForeignKey(name = "fk_grant_faculty") )
	 */
	@NotNull(message = "Project is required")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "FacultyId")
	private Faculty faculty;
 
	@NotNull(message = "Grant amount is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
	@Digits(integer = 18, fraction = 2)
	@Column(name = "Amount", nullable = false, precision = 18, scale = 2)
	private BigDecimal amount;
 
	@Column(name = "Date", nullable = false)
	private LocalDate date;
 
	@NotNull(message = "Grant status is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "Status", nullable = false, length = 32)
	private GrantStatus status=GrantStatus.UNDER_REVIEW;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_ProgramManager_id",referencedColumnName="userId")
    private User user;
}
