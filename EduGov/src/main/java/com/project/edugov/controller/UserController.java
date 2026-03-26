package com.project.edugov.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.edugov.config.JwtUtil;
import com.project.edugov.dto.UserResponseDTO;
import com.project.edugov.model.Role;
import com.project.edugov.model.Status;
import com.project.edugov.model.User;
import com.project.edugov.service.AuditServiceImpl;
import com.project.edugov.service.BlackListedTokenService;
import com.project.edugov.service.UserService;

// ---> NEW EXCEPTION IMPORT <---
import com.project.edugov.exception.EduGovExceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;
    private final BlackListedTokenService blackListService;
    private final AuditServiceImpl auditService;

    public UserController(UserService userService, ModelMapper modelMapper, JwtUtil jwtUtil, BlackListedTokenService blackListService,AuditServiceImpl auditService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.blackListService = blackListService;
        this.auditService = auditService;
    }

    public record LoginRequest(String email, String password) {}
    public record PasswordResetRequest(String email, String newPassword) {}
    public record StatusUpdateRequest(Status status) {}
    public record JwtAuthResponse(String token, UserResponseDTO user) {}

    private UserResponseDTO mapToDTO(User user) {
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest request) {
        logger.info("REST request to login user: {}", request.email());
        
        User authenticatedUser = userService.authenticate(request.email(), request.password());
        String token = jwtUtil.generateToken(authenticatedUser.getEmail(), authenticatedUser.getRole().name());
        UserResponseDTO userDTO = mapToDTO(authenticatedUser);
        auditService.logActionForUser(authenticatedUser, "LOGIN", "AUTH_MODULE");
        return ResponseEntity.ok(new JwtAuthResponse(token, userDTO));
    }

    @GetMapping("/recoverEmail")
    public ResponseEntity<String> recoverEmail(@RequestParam String phone) {
        logger.info("REST request to recover email for phone: {}", phone);
        String email = userService.recoverEmailByPhone(phone);
        return ResponseEntity.ok(email);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        logger.info("REST request to reset password for user: {}", request.email());
        userService.updatePassword(request.email(), request.newPassword());
        return ResponseEntity.ok("Password updated successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        logger.info("REST request to get User by ID: {}", id);
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(mapToDTO(user)))
                .orElseThrow(() -> {
                    logger.error("REST request failed: User not found with ID {}", id);
                    // UPDATED
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUserByRole(@PathVariable Role role) {
        logger.info("REST request to get Users by Role: {}", role);
        List<UserResponseDTO> users = userService.getUserByRole(role).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserResponseDTO>> getUserByStatus(@PathVariable Status status) {
        logger.info("REST request to get Users by Status: {}", status);
        List<UserResponseDTO> users = userService.getUserByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<UserResponseDTO> updateUserStatus(
            @PathVariable Long id, 
            @RequestBody StatusUpdateRequest request) {
        logger.info("REST request to update status for User ID {} to {}", id, request.status());
        User updatedUser = userService.updateUserStatus(id, request.status());
        auditService.logAction("UPDATE_STATUS_TO_" + request.status(), "USER_ID_" + id);
        return ResponseEntity.ok(mapToDTO(updatedUser));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        logger.info("REST request to logout user");
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            auditService.logAction("LOGOUT", "AUTH_MODULE");
            blackListService.addToBlacklist(jwt);
            SecurityContextHolder.clearContext();
            
            logger.info("Successfully logged out and blacklisted token.");
            return ResponseEntity.ok("Logout successful. Token has been invalidated.");
        }
        
        logger.warn("Logout failed: No valid Bearer token provided in header");
        return ResponseEntity.badRequest().body("No valid token provided for logout.");
    }
}