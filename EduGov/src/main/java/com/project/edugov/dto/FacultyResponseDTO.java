package com.project.edugov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Essential for ModelMapper to instantiate the DTO
@AllArgsConstructor // Useful for manual testing or builders
public class FacultyResponseDTO {

    private Long facultyId;   // Matches the Database Long ID (No "FAC-" prefix)
    private String email;      // Set manually in Service from the User entity
    private String department; // Automatically mapped by ModelMapper
    private String status;     //
    private String dob;        
	private String name;
	private String phone;
	
}
