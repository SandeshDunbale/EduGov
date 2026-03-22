package com.project.edugov.controller;

import com.project.edugov.dto.ComplianceRecordDTO;
import com.project.edugov.service.ComplianceRecordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compliance")
public class ComplianceRecordController {

    @Autowired
    private ComplianceRecordService service;

    @PostMapping("/create")
    public ComplianceRecordDTO create(@RequestBody ComplianceRecordDTO dto) {
        return service.createRecord(dto);
    }

    @GetMapping("/all")
    public List<ComplianceRecordDTO> getAll() {
        return service.getAllRecords();
    }

    @GetMapping("/type/{type}")
    public List<ComplianceRecordDTO> getByType(@PathVariable String type) {
        return service.getByType(type);
    }

    @GetMapping("/result/{result}")
    public List<ComplianceRecordDTO> getByResult(@PathVariable String result) {
        return service.getByResult(result);
    }

	public ComplianceRecordService getService() {
		return service;
	}

	public void setService(ComplianceRecordService service) {
		this.service = service;
	}
}

