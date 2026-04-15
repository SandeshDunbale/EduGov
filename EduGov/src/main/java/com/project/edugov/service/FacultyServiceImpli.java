package com.project.edugov.service;

import com.project.edugov.dto.FacultyDTO;
import com.project.edugov.dto.FacultyResponseDTO;
import com.project.edugov.exception.ResourceNotFoundException;
import com.project.edugov.model.Faculty;
import com.project.edugov.model.Role;
import com.project.edugov.model.Status;
import com.project.edugov.model.User;
import com.project.edugov.repository.FacultyRepository;
import com.project.edugov.repository.UserRepository;
import com.project.edugov.service.FacultyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacultyServiceImpli implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public FacultyResponseDTO registerFaculty(FacultyDTO dto) {
        log.info("Attempting to register faculty with email: {}", dto.getEmail());

       
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(encoder.encode(dto.getPassword()));
        user.setRole(Role.FACULTY);
        user.setStatus(Status.PENDING);
        User savedUser = userRepository.save(user);
        log.debug("User record saved. ID: {}", savedUser.getUserId());

       
        Faculty faculty = modelMapper.map(dto, Faculty.class);
        faculty.setUser(savedUser); 
        faculty.setStatus(Status.PENDING);
        
        Faculty savedFaculty = facultyRepository.save(faculty);
        log.info("Faculty record created. ID: {}", savedFaculty.getFacultyId());

        return convertToResponseDTO(savedFaculty);
    }

    @Override
    public Optional<FacultyResponseDTO> getFacultyById(Long id) {
        log.info("Fetching faculty ID: {}", id);
        return facultyRepository.findById(id).map(this::convertToResponseDTO);
    }

    @Override
    public List<FacultyResponseDTO> getFacultyByStatus(Status status) {
        List<Faculty> faculties = (status == null) ? 
                facultyRepository.findAll() : facultyRepository.findByStatus(status);
        
        return faculties.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FacultyResponseDTO updateFaculty(Long id, FacultyDTO dto) {
        
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + id));


        User user = faculty.getUser();

        // 3. Update the Faculty fields from the DTO
        faculty.setName(dto.getName());
        faculty.setDob(dto.getDob());
        faculty.setDepartment(dto.getDepartment());
       
        faculty.setPhone(dto.getPhone()); 

        
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

      
        faculty.setUser(user);

      
        Faculty updatedFaculty = facultyRepository.save(faculty);
        
        return convertToResponseDTO(updatedFaculty);
    }


  
    @Override
    @Transactional
    public FacultyResponseDTO approveFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        faculty.setStatus(Status.APPROVE);
        User user=faculty.getUser();
        user.setStatus(Status.APPROVE);
         // Keep both in sync
        notificationService.createNotification(
        		user.getUserId(),                           // userId
                user.getUserId(),                           // entityId (Using userId since this is a user-level event)
                "Registration Succesful as Faculty. Please Login.", // message
                "SECURITY",                             // category
                 user.getEmail()
        		);
        return convertToResponseDTO(facultyRepository.save(faculty));
    }

    @Override
    @Transactional
    public FacultyResponseDTO declineFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        faculty.setStatus(Status.DECLINED);
        User user=faculty.getUser();
        user.setStatus(Status.APPROVE);
         // Keep both in sync
        notificationService.createNotification(
        		user.getUserId(),                           // userId
                user.getUserId(),                           // entityId (Using userId since this is a user-level event)
                "Registration Failed as Faculty.", // message
                "SECURITY",                             // category
                 user.getEmail()
        		);
        return convertToResponseDTO(facultyRepository.save(faculty));
    }

    @Override
    @Transactional
    public void deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        
        User user = faculty.getUser();

        // Delete Child first, then Parent
        facultyRepository.delete(faculty);
        if (user != null) {
            userRepository.delete(user);
        }
        log.warn("Permanently deleted faculty and user associated with Faculty ID: {}", id);
    }

    // --- Helper for Consistent Mapping ---
    private FacultyResponseDTO convertToResponseDTO(Faculty faculty) {
        FacultyResponseDTO resp = modelMapper.map(faculty, FacultyResponseDTO.class);
        if (faculty.getUser() != null) {
            resp.setName(faculty.getUser().getName());
            resp.setEmail(faculty.getUser().getEmail());
            resp.setPhone(faculty.getUser().getPhone());
            resp.setStatus(faculty.getStatus().name());
        }
        return resp;
    }
}