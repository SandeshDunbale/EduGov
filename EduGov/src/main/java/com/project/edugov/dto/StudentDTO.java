package com.project.edugov.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "DOB is required")
    private LocalDate dob;

    private String gender;
    private String address;
}