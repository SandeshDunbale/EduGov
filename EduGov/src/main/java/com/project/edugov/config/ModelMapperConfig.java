package com.project.edugov.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.project.edugov.dto.FacultyMinimalDTO;
import com.project.edugov.dto.GrantApplicationDTO;
import com.project.edugov.dto.GrantResponseDTO;
import com.project.edugov.model.Faculty;
import com.project.edugov.model.Grant;
import com.project.edugov.model.GrantApplication;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // Configuration: STRICT mapping ensures it only maps fields with EXACT matching names.
        // This prevents accidental data leaks if fields have similar but not exact names.
        modelMapper.getConfiguration()
                   .setMatchingStrategy(MatchingStrategies.STRICT)
                   .setFieldMatchingEnabled(true)
                   .setSkipNullEnabled(true); // Don't overwrite existing values with nulls
        
        
        //Module 4 Model Mapper config-->Doing it cz same file name
        modelMapper.typeMap(Faculty.class, FacultyMinimalDTO.class).addMappings(mapper -> {
			mapper.map(src -> src.getUser().getName(), FacultyMinimalDTO::setFacultyName);
		});

		modelMapper.typeMap(Grant.class, GrantResponseDTO.class).addMappings(mapper -> {

			mapper.map(src -> src.getApprovedBy().getRole(), GrantResponseDTO::setApprovedByRole);
			mapper.map(src -> src.getProject().getTitle(), GrantResponseDTO::setProjectTitle);
		});

		modelMapper.typeMap(GrantApplication.class, GrantApplicationDTO.class).addMappings(mapper -> {
			mapper.map(src -> src.getProject().getProjectId(), GrantApplicationDTO::setProjectId);
			mapper.map(src -> src.getProject().getTitle(), GrantApplicationDTO::setProjectTitle);
		});
		
        return modelMapper;
    }
}