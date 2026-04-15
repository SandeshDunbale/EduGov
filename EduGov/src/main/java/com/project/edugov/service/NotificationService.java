package com.project.edugov.service;
 
import com.project.edugov.exception.ResourceNotFoundException;
import com.project.edugov.model.Notification;
import com.project.edugov.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.time.LocalDateTime;
import java.util.List;
 
@Service
public class NotificationService {
 
    @Autowired
    private NotificationRepository notificationRepository;
 
    @Autowired
    private EmailService emailService;
 
    // ✅ CREATE + SEND EMAIL
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
 
        // Send Email
        emailService.sendEmail(
                email,
                "EduGov Notification",
                message
        );
 
        return saved;
    }
 
    // ✅ GET ALL
    public List<Notification> getAll() {
        List<Notification> list = notificationRepository.findAll();
 
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("No notifications found");
        }
 
        return list;
    }
 
    // ✅ GET BY USER
    public List<Notification> getByUser(Long userId) {
        List<Notification> list = notificationRepository.findByUserId(userId);
 
        if (list.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No notifications found for userId: " + userId
            );
        }
 
        return list;
    }
 
    // ✅ GET BY CATEGORY
    public List<Notification> getByCategory(String category) {
        List<Notification> list = notificationRepository.findByCategory(category);
 
        if (list.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No notifications found for category: " + category
            );
        }
 
        return list;
    }
 
    // ✅ MARK AS READ
    public Notification markAsRead(Long id) {
 
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found with id: " + id)
                );
 
        notification.setStatus("READ");
 
        return notificationRepository.save(notification);
    }
}
