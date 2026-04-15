package com.project.edugov.controller;
 
import com.project.edugov.model.Notification;
import com.project.edugov.service.NotificationService;
import com.project.edugov.service.AuditServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/notifications") //it tells which url should call which method

public class NotificationController {
    @Autowired
    private  NotificationService notificationService;

    @Autowired
    private AuditServiceImpl auditService;
 
    // CREATE
    @PostMapping  //http://localhost:1234/api/notifications
    public Notification createNotification(
            @RequestParam Long userId,
            @RequestParam Long entityId,
            @RequestParam String message,
            @RequestParam String category,
            @RequestParam String email) {
 
        auditService.logAction("CREATE_NOTIFICATION", "USER_ID_" + userId);

        return notificationService.createNotification(
                userId, entityId, message, category, email
        );
    }
 
    // GET ALL
    @GetMapping  //http://localhost:1234/api/notifications
    public List<Notification> getAll() {
        return notificationService.getAll();
    }
 
    // GET BY USER
    @GetMapping("/user/{userId}") //http://localhost:1234/api/notifications/user/1
    public List<Notification> getByUser(@PathVariable Long userId) {
        return notificationService.getByUser(userId);
    }
 
    // GET BY CATEGORY   http://localhost:1234/api/notifications/category/{category}
    @GetMapping("/category{category}")
    public List<Notification> getByCategory(@RequestParam String category) {
        return notificationService.getByCategory(category);
    }
 
    // MARK AS READ
    @PostMapping("/read/{id}")   //http://localhost:1234/api/notifications/read/{id}
    public Notification markAsRead(@PathVariable Long id) {
        auditService.logAction("MARK_NOTIFICATION_READ", "NOTIFICATION_ID_" + id);
        return notificationService.markAsRead(id);
    }
}