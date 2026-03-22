package com.project.edugov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.project.edugov.dto.AuditDTO;
import com.project.edugov.model.Audit;
import com.project.edugov.service.AuditService;

@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private AuditService service;

    @PostMapping("/create")
    public Audit create(@RequestBody AuditDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/all")
    public List<Audit> getAll() {
        return service.getAll();
    }
}