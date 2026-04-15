package com.project.edugov.service;

import java.util.List;
import java.util.Optional;

import com.project.edugov.model.Role;
import com.project.edugov.model.Status;
import com.project.edugov.model.User;

public interface UserService {
	User authenticate(String email,String rawPassword);
	List<User> getUserByRole(Role role);
	List<User> getUserByStatus(Status status);
	User updateUserStatus(Long userId,Status newStatus);
	Optional<User> getUserById(Long userId);
	String recoverEmailByPhone(String phone);
	void updatePassword(String email,String newRawPassword);
}
