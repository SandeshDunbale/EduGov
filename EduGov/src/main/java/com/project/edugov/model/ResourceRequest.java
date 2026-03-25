package com.project.edugov.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data

@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // JPA requires a no-arg ctor
@AllArgsConstructor
@Builder
@Entity


@Table(name = "resourcerequest")
public class ResourceRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RequestID")
	private Long requestId;

	/** Who raised the request (Student/Faculty user) */

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "RequesterUserID", referencedColumnName = "userId", nullable = false)
	private User requester;

	/** Optional strong FK to a Resource when ItemType = RESOURCE */

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ResourceID")
	private Resource resource;

	/** Optional strong FK to an Infrastructure when ItemType = INFRASTRUCTURE */

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "InfraID")
	private Infrastructure infrastructure;

	/** For quick filtering and UI rendering */

	@Enumerated(EnumType.STRING)
	@Column(name = "item_type", length = 16, nullable = false)
	private RequestItemType itemType; // RESOURCE / INFRASTRUCTURE

	/** For resource quantity; null for infrastructure requests */

	@Column(name = "quantity")
	private Integer quantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 16, nullable = false)
	private RequestStatus status; // SUBMITTED / IN_PROGRESS / APPROVED / DECLINED

	/** Decision maker (University Admin as Officer) */

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ApprovedBy")
	private User approvedBy;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false, nullable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

	@Column(name = "decision_at")
	private Instant decisionAt;

	/**
	 * 
	 * Guard: ensure exactly one of (resource, infrastructure) is set and matches
	 * itemType.
	 * 
	 */

	@PrePersist
	@PreUpdate
	private void validatePolymorphicTarget() {
		boolean hasResource = (resource != null);
		boolean hasInfra = (infrastructure != null);
		if (itemType == null) {
			throw new IllegalStateException("itemType is required");

		}

		// must be exactly one target

		if (hasResource == hasInfra) {
			throw new IllegalStateException("Exactly one of Resource or Infrastructure must be set");

		}

		// match itemType with the target set

		if (itemType == RequestItemType.RESOURCE && !hasResource) {

			throw new IllegalStateException("itemType=RESOURCE requires Resource to be set");

		}

		if (itemType == RequestItemType.INFRASTRUCTURE && !hasInfra) {

			throw new IllegalStateException("itemType=INFRASTRUCTURE requires Infrastructure to be set");

		}

		// quantity should be null for infrastructure; positive for resource (if
		// provided)

		if (itemType == RequestItemType.INFRASTRUCTURE) {

			if (quantity != null) {

				throw new IllegalStateException("quantity must be null for infrastructure requests");

			}

		} else if (itemType == RequestItemType.RESOURCE) {

			if (quantity == null || quantity <= 0) {

				throw new IllegalStateException("quantity must be > 0 for resource requests");

			}

		}

	}

}
