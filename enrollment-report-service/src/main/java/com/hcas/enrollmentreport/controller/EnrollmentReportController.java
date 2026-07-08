package com.hcas.enrollmentreport.controller;

import com.hcas.enrollmentreport.dto.ReportRunDto;
import com.hcas.enrollmentreport.service.EnrollmentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/enrollment-error-report")
@RequiredArgsConstructor
public class EnrollmentReportController {

    private final EnrollmentReportService reportService;

    @PostMapping
    public ReportRunDto runReport() {
        return reportService.runReport();
    }
}