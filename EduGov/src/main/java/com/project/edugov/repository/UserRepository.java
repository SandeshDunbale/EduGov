// src/com.project.edugov/repository/UserRepository.java
package com.project.edugov.repository;

import com.project.edugov.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {}