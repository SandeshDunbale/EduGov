package com.project.edugov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.edugov.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}