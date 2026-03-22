package com.project.edugov.service;

import java.util.List;
import com.project.edugov.dto.ComplianceRecordDTO;

public interface ComplianceRecordService {

    ComplianceRecordDTO createRecord(ComplianceRecordDTO dto);

    List<ComplianceRecordDTO> getAllRecords();

    List<ComplianceRecordDTO> getByType(String type);

    List<ComplianceRecordDTO> getByResult(String result);
}



