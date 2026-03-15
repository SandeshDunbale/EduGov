package com.project.edugov.repository;

import com.edugov.notifications.model.Notification;
import com.edugov.notifications.model.Notification.Category; // Explicit import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Matches field: userID
    List<Notification> findByUserIDOrderByCreatedDateDesc(Long userID);

    // Matches fields: userID and status
    List<Notification> findByUserIDAndStatus(Long userID, String status);

    // Matches fields: userID and category
    List<Notification> findByUserIDAndCategory(Long userID, Category category);
}
  

