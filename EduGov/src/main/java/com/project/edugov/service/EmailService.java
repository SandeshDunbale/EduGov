package com.project.edugov.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Async
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("mrunalip263@gmail.com"); // explicitly set the sender
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            
            // If successful, this will print in your IDE console
            System.out.println("✅ SUCCESS: Email sent to " + toEmail);
            
        } catch (Exception e) {
            // If it fails, this will print the EXACT reason in your IDE console
            System.err.println("❌ FAILED to send email to " + toEmail);
            System.err.println("Error details: " + e.getMessage());
        }
    }
}