package com.hcas.enrollmentreport.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "enrollment-report")
public class EnrollmentReportConfig {
    private String inputFilePath;
    private String outputFilePath;
    private int maxLinesPerPage;
}