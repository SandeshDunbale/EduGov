package com.project.edugov.repository;

import com.project.edugov.model.Faculty;
import com.project.edugov.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    // Used by Admin to see who needs approval
    List<Faculty> findByStatus(Status status);
}
