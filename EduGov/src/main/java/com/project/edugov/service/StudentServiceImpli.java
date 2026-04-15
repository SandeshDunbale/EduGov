package com.project.edugov.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.edugov.dto.StudentDTO;
import com.project.edugov.dto.StudentResponseDTO;
import com.project.edugov.exception.ResourceNotFoundException;
import com.project.edugov.model.Role;
import com.project.edugov.model.Status;
import com.project.edugov.model.Student;
import com.project.edugov.model.User;
import com.project.edugov.repository.StudentRepository;
import com.project.edugov.repository.UserRepository;

//import com.project.edugov.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StudentServiceImpli implements StudentService {

    private final StudentRepository studentRepo;
    private final UserRepository userRepo;
    private final ModelMapper mapper;
    private final BCryptPasswordEncoder encoder;
    private final NotificationService notificationService;

    @Override
    public StudentResponseDTO registerStudent(StudentDTO dto) {
        log.info("SERVICE: Initiating registration for email: {}", dto.getEmail());
        
        if (userRepo.existsByEmail(dto.getEmail())) {
            log.error("SERVICE: Registration failed - Email {} already in use", dto.getEmail());
            throw new ResourceNotFoundException("Email already exists: " + dto.getEmail());
        }

        
        //creating anew user to save
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(encoder.encode(dto.getPassword()));
        user.setRole(Role.STUDENT);
        user.setStatus(Status.PENDING);
        user = userRepo.save(user);//creating idntity
        log.info("SERVICE: User record created with ID: {}", user.getUserId());

        
         //using moddel mapper for student
        Student student = mapper.map(dto, Student.class);//creating profile
        student.setUser(user);//connecting
        student.setStatus(Status.PENDING);
        
        Student savedStudent = studentRepo.save(student);//call insert method into table
        log.info("SERVICE: Student record created with ID: {}", savedStudent.getStudentId());
        
        return convertToResponseDTO(savedStudent);
    }

    @Override
    public List<StudentResponseDTO> getStudentsByStatus(Status status) {
        log.info("SERVICE: Fetching all students with status: {}", status);
        return studentRepo.findByStatus(status).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public StudentResponseDTO approveStudent(Long id) {
        log.info("SERVICE: Approving Student ID: {}", id);
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found ID: " + id));
        
        student.setStatus(Status.APPROVE);
        if (student.getUser() != null) {
        	User user=student.getUser();
        	user.setStatus(Status.APPROVE);
        
        
        log.info("SERVICE: Student ID {} status set to APPROVED", id);
        notificationService.createNotification(
        		user.getUserId(),                           // userId
                user.getUserId(),                           // entityId (Using userId since this is a user-level event)
                "Registration Sucessful. You can Login Now.", // message
                "SECURITY",                             // category
                user.getEmail() 
                );
        }
        return convertToResponseDTO(studentRepo.save(student));
    }

    @Override
    public StudentResponseDTO declineStudent(Long id) {
        log.warn("SERVICE: Declining Student ID: {}", id);
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found ID: " + id));
        
        student.setStatus(Status.REJECT);
        if (student.getUser() != null) {
        	User user=student.getUser();
        	user.setStatus(Status.REJECT);
        
        notificationService.createNotification(
            	user.getUserId(),                           // userId
                user.getUserId(),                           // entityId (Using userId since this is a user-level event)
                "Registration Failed.", // message
                "SECURITY",                             // category
                 user.getEmail() 
               );
            }
        
        return convertToResponseDTO(studentRepo.save(student));
    }

    @Override
    public StudentResponseDTO updateStudent(Long id, StudentDTO dto) {
        log.info("SERVICE: Updating profile for Student ID: {}", id);
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found ID: " + id));
        
        mapper.map(dto, student);
        if (student.getUser() != null) {
            student.getUser().setName(dto.getName());
            student.getUser().setPhone(dto.getPhone());
        }
        
        log.info("SERVICE: Profile update complete for Student ID: {}", id);
        return convertToResponseDTO(studentRepo.save(student));
    }

    @Override
    public void deleteStudent(Long id) {
        log.error("SERVICE: Request to delete Student ID: {}", id);
        if (!studentRepo.existsById(id)) {
            log.warn("SERVICE: Delete failed - ID {} does not exist", id);
            throw new ResourceNotFoundException("ID not found: " + id);
        }
        studentRepo.deleteById(id);
        log.info("SERVICE: Student ID {} and linked user deleted successfully", id);
    }

    @Override
    public Optional<StudentResponseDTO> getStudentById(Long id) {
        return studentRepo.findById(id).map(this::convertToResponseDTO);
    }

    @Override
    public Optional<StudentResponseDTO> getStudentByBusinessId(String studentId) {
        try {
            return getStudentById(Long.parseLong(studentId));
        } catch (NumberFormatException e) {
            log.error("SERVICE: Invalid ID format: {}", studentId);
            return Optional.empty();
        }
    }

    
    //helper method to convert data into required data 
    private StudentResponseDTO convertToResponseDTO(Student student) {
        StudentResponseDTO result = mapper.map(student, StudentResponseDTO.class);
        if (student.getUser() != null) {
            result.setName(student.getUser().getName());
            result.setEmail(student.getUser().getEmail());
            result.setPhone(student.getUser().getPhone());
        }
        return result;
    }
}