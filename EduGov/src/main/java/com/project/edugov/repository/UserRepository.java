package com.project.edugov.repository;
 
import com.project.edugov.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface UserRepository extends JpaRepository<User, Long> {
}