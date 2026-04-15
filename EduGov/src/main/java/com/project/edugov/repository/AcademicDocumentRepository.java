package com.project.edugov.repository;

import com.project.edugov.model.AcademicDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AcademicDocumentRepository extends JpaRepository<AcademicDocument, Long> {
    // Allows you to fetch all 5+ documents for one user
    List<AcademicDocument> findByUserUserId(Long userId);
}
