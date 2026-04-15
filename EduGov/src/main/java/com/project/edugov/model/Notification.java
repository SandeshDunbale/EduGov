package com.project.edugov.model;
 
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
 
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
 
    private Long userId;
    private Long entityId;
 
    private String message;
    private String category; // PROGRAM / GRANT / COURSE / RESOURCE
 
    private String status; // SENT / FAILED
 
    private LocalDateTime createdDate;

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
    
}
 