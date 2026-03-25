package com.project.edugov.controller;
 
import com.project.edugov.model.Report;
import com.project.edugov.model.ReportScope;
import com.project.edugov.service.ReportService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
	
 @Autowired
    private  ReportService reportService;
 
    @PostMapping   //http://localhost:1234/api/reports?scope=PROGRAM,  http://localhost:1234/api/reports?scope=PROJECT, http://localhost:1234/api/reports?scope=GRANT
    public Report generateReport(@RequestParam ReportScope scope) {
        return reportService.generateReport(scope);
    }
}