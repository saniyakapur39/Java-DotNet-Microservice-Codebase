package com.hcas.enrollmentreport.repository;

import com.hcas.enrollmentreport.model.AuditEnrollmentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditEnrollmentReportRepository extends JpaRepository<AuditEnrollmentReport, Long> {
    AuditEnrollmentReport findByJobId(UUID jobId);
}