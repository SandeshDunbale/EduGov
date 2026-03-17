package com.project.edugov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.User;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
}

