package com.project.edugov.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentResponseDTO {
    private Long studentId;
    private String name;
    private String email;
    private String status;
    private String phone;
    private LocalDate dob;
    private String address;
}