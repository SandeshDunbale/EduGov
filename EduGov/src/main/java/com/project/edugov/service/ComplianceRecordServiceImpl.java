package com.project.edugov.service;

import com.project.edugov.dto.ComplianceRecordDTO;
import com.project.edugov.model.ComplianceRecord;
import com.project.edugov.model.User;
import com.project.edugov.repository.ComplianceRecordRepository;
import com.project.edugov.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplianceRecordServiceImpl implements ComplianceRecordService {

    private static final Logger logger = LoggerFactory.getLogger(ComplianceRecordServiceImpl.class);

    @Autowired
    private ComplianceRecordRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ComplianceRecordDTO createRecord(ComplianceRecordDTO dto) {

        logger.info("Creating compliance record");

        ComplianceRecord record = new ComplianceRecord();
        
        record.setEntityId(dto.getEntityId());
        record.setEntityType(dto.getEntityType());
        record.setResult(dto.getResult());
        record.setDate(dto.getDate());
        record.setNotes(dto.getNotes());

        if (dto.getOfficerId() != null) {
            User user = userRepository.findById(dto.getOfficerId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            record.setOfficer(user);
        }

        ComplianceRecord saved = repository.save(record);

        logger.info("Compliance record saved with ID: {}", saved.getComplianceId());

        return modelMapper.map(saved, ComplianceRecordDTO.class);
    
    }

    @Override
    public List<ComplianceRecordDTO> getAllRecords() {

        logger.info("Fetching all compliance records");

        return repository.findAll()
                .stream()
                .map(record -> modelMapper.map(record, ComplianceRecordDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplianceRecordDTO> getByType(String type) {

        logger.info("Fetching records by type: {}", type);

        return repository.findByEntityType(type)
                .stream()
                .map(record -> modelMapper.map(record, ComplianceRecordDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplianceRecordDTO> getByResult(String result) {

        logger.info("Fetching records by result: {}", result);

        return repository.findByResult(result)
                .stream()
                .map(record -> modelMapper.map(record, ComplianceRecordDTO.class))
                .collect(Collectors.toList());
    }

	public ComplianceRecordRepository getRepository() {
		return repository;
	}

	public void setRepository(ComplianceRecordRepository repository) {
		this.repository = repository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public ModelMapper getModelMapper() {
		return modelMapper;
	}

	public void setModelMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public static Logger getLogger() {
		return logger;
	}
}
