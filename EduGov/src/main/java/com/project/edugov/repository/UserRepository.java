package com.project.edugov.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.project.edugov.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
