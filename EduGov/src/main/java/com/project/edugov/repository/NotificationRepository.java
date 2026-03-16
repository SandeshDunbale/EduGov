package com.project.edugov.repository;

import java.util.List;
import java.util.Locale.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Matches field: userID
    List<Notification> findByUserIDOrderByCreatedDateDesc(Long userID);

    // Matches fields: userID and status
    List<Notification> findByUserIDAndStatus(Long userID, String status);

    // Matches fields: userID and category
    List<Notification> findByUserIDAndCategory(Long userID, Category category);
}
  

