package com.project.edugov.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class FacultyDTO {
    
    // Added Name: Crucial for the User entity link
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    // Faculty specific field
    @NotBlank(message = "Department is required")
    private String department;
    
    @NotNull(message = "Date of Birth is required")
    @Past(message = "DOB must be in the past")
    private LocalDate dob;
    
}