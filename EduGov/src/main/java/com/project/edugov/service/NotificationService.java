package com.project.edugov.service;
 
import com.project.edugov.model.Notification;
import com.project.edugov.repository.NotificationRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.time.LocalDateTime;
import java.util.List;
 
@Service
public class NotificationService {
    
	@Autowired
    private  NotificationRepository notificationRepository;
	@Autowired	
    private  EmailService emailService;
 
    // CREATE + EMAIL
    public Notification createNotification(Long userId,
                                           Long entityId,
                                           String message,
                                           String category,
                                           String email) {
 
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setEntityId(entityId);
        notification.setMessage(message);
        notification.setCategory(category);
        notification.setStatus("UNREAD");
        notification.setCreatedDate(LocalDateTime.now());
 
        Notification saved = notificationRepository.save(notification);
 
        // ✅ SEND EMAIL
        emailService.sendEmail(
                email,
                "EduGov Notification",
                message
        );
 
        return saved;
    }
 
    // GET ALL
    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }
 
    // GET BY USER
    public List<Notification> getByUser(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
 
    // GET BY CATEGORY
    public List<Notification> getByCategory(String category) {
        return notificationRepository.findByCategory(category);
    }
 
    // MARK AS READ
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
 
        notification.setStatus("READ");
        return notificationRepository.save(notification);
    }
}