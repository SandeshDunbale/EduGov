package com.project.edugov.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.edugov.model.Status;
import com.project.edugov.model.Student;



@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
//    Optional<Student> findByStudentId(String studentId);
    
    List<Student> findByStatus(Status status);
//    long countByProgram_ProgramID(Long programId);
//    @Query(value = "SELECT COUNT(*) FROM student WHERE program_id = :programId", nativeQuery = true)
//    Long countByProgram_ProgramID(@Param("programId") Long programId);
}

