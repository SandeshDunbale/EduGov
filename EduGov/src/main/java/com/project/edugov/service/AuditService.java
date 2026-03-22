package com.project.edugov.service;

import java.util.List;
import com.project.edugov.dto.AuditDTO;
import com.project.edugov.model.Audit;

public interface AuditService {

    Audit create(AuditDTO dto);

    List<Audit> getAll();
}