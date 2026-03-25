package com.project.edugov.controller;
 
import com.project.edugov.model.Notification;
import com.project.edugov.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/notifications")

public class NotificationController {
    @Autowired
    private  NotificationService notificationService;
 
    // CREATE
    @PostMapping  //http://localhost:1234/api/notifications
    public Notification createNotification(
            @RequestParam Long userId,
            @RequestParam Long entityId,
            @RequestParam String message,
            @RequestParam String category,
            @RequestParam String email) {
 
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
 
    // GET BY CATEGORY
    @GetMapping("/category")
    public List<Notification> getByCategory(@RequestParam String category) {
        return notificationService.getByCategory(category);
    }
 
    // MARK AS READ
    @PostMapping("/read/{id}")
    public Notification markAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }
}