package com.hcas.enrollmentreport.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportRunDto {
    private String jobId;
    private String status;
    private String reportPath;
}