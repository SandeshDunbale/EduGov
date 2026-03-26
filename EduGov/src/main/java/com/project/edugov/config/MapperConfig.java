package com.project.edugov.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.project.edugov.dto.FacultyMinimalDTO;
import com.project.edugov.dto.GrantResponseDTO;
import com.project.edugov.model.Faculty;
import com.project.edugov.model.Grant;
import com.project.edugov.model.GrantApplication;

@Configuration
public class MapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		modelMapper.typeMap(Faculty.class, FacultyMinimalDTO.class).addMappings(mapper -> {
			mapper.map(src -> src.getUser().getName(), FacultyMinimalDTO::setFacultyName);
		});

		modelMapper.typeMap(Grant.class, GrantResponseDTO.class).addMappings(mapper -> {

			mapper.map(src -> src.getApprovedBy().getRole(), GrantResponseDTO::setApprovedByRole);
			mapper.map(src -> src.getProject().getTitle(), GrantResponseDTO::setProjectTitle);
		});

		modelMapper.typeMap(GrantApplication.class, GrantResponseDTO.class).addMappings(mapper -> {

			mapper.map(src -> src.getProject().getTitle(), GrantResponseDTO::setProjectTitle);
		});
		return modelMapper;
	}
}