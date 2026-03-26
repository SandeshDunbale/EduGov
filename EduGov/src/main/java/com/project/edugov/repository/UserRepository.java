package com.project.edugov.repository;

import com.project.edugov.model.User;
import com.project.edugov.model.Role;
import com.project.edugov.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	// Primary lookup for the login process
	Optional<User> findByEmail(String email);

	// Check if an email is already in use
	boolean existsByEmail(String email);

	// Find users by their assigned role (Auditor, Admin, etc.)
	List<User> findByRole(Role role);

	// Find users by status (useful for filtering active vs. pending accounts)
	List<User> findByStatus(Status status);

	// Find a specific user by email and ensure they are active before login
	Optional<User> findByEmailAndStatus(String email, Status status);

	// Helps "Forgot Email" by finding the account via phone number
	Optional<User> findByPhone(String phone);

	// Check if phone exists for recovery validation
	boolean existsByPhone(String phone);
}