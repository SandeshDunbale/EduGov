package com.project.edugov.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.project.edugov.dto.AuditDTO;
import com.project.edugov.model.Audit;
import com.project.edugov.model.User;
import com.project.edugov.repository.AuditRepository;
import com.project.edugov.repository.UserRepository;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private AuditRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Audit create(AuditDTO dto) {

        User officer = userRepository.findById(dto.getOfficerId()).orElse(null);

        Audit audit = new Audit();
        audit.setScope(dto.getScope());
        audit.setFindings(dto.getFindings());
        audit.setStatus(dto.getStatus());
        audit.setDate(dto.getDate());
        audit.setOfficer(officer);

        return repository.save(audit);
    }

    @Override
    public List<Audit> getAll() {
        return repository.findAll();
    }
}