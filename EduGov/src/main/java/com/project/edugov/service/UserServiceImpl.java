package com.project.edugov.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.edugov.model.Role;
import com.project.edugov.model.Status;
import com.project.edugov.model.User;
import com.project.edugov.repository.UserRepository;

// ---> NEW EXCEPTION IMPORTS <---
import com.project.edugov.exception.EduGovExceptions.ResourceNotFoundException;
import com.project.edugov.exception.EduGovExceptions.AccountNotActiveException;
import com.project.edugov.exception.EduGovExceptions.InvalidCredentialsException;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User authenticate(String email, String rawPassword) {
        logger.info("Attempting authentication for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Authentication failed: No user found with email {}", email);
                    // UPDATED
                    return new ResourceNotFoundException("Invalid Email or Password"); 
                });

        if (user.getStatus() != Status.ACTIVE) {
            logger.warn("Authentication failed: User {} is currently {}", email, user.getStatus());
            // UPDATED
            throw new AccountNotActiveException("Account is Currently " + user.getStatus());
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            logger.warn("Authentication failed: Invalid password entered for user {}", email);
            // UPDATED
            throw new InvalidCredentialsException("Invalid email or password");
        }

        logger.info("Authentication successful for user: {}", email);
        return user;
    }

    @Override
    public List<User> getUserByRole(Role role) {
        logger.debug("Fetching users with role: {}", role);
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> getUserByStatus(Status status) {
        logger.debug("Fetching users with status: {}", status);
        return userRepository.findByStatus(status);
    }

    @Override
    public User updateUserStatus(Long userId, Status newStatus) {
        logger.info("Attempting to update status for user ID {} to {}", userId, newStatus);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Failed to update status: User not found with ID {}", userId);
                    // UPDATED
                    return new ResourceNotFoundException("User not found with ID:" + userId);
                });
                
        user.setStatus(newStatus);
        User savedUser = userRepository.save(user);
        logger.info("Successfully updated status for user ID {}", userId);
        return savedUser;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        logger.debug("Fetching user by ID: {}", userId);
        return userRepository.findById(userId);
    }

    @Override
    public String recoverEmailByPhone(String phone) {
        logger.info("Attempting to recover email for phone number: {}", phone);
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> {
                    logger.warn("Email recovery failed: No account found for phone number {}", phone);
                    // UPDATED
                    return new ResourceNotFoundException("No account found with this phone number.");
                });
                
        logger.info("Successfully recovered email for phone number: {}", phone);
        return user.getEmail();
    }

    @Override
    public void updatePassword(String email, String newRawPassword) {
        logger.info("Attempting to update password for user: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("Password update failed: User not found with email {}", email);
            // UPDATED
            return new ResourceNotFoundException("User not found.");
        });
        
        user.setPasswordHash(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
        logger.info("Successfully updated password for user: {}", email);
    }
}