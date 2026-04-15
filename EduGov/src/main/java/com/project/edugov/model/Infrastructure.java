package com.project.edugov.model;



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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "infrastructure")

public class Infrastructure {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "InfraID")
	private Long infraId;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "ProgramID", referencedColumnName="programID",nullable = false)
	private Program program;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", length = 16, nullable = false)
	private InfrastructureType type; // LIBRARY/LAB/CENTER

	@Column(name = "location", length = 200)
	private String location;

	@Column(name = "capacity")
	private Integer capacity;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 16, nullable = false)
	private InfrastructureStatus status; // AVAILABLE/IN_USE/MAINTENANCE/RETIRED


}
