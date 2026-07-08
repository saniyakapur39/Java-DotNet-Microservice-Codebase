package com.hcas.enrollmentreport.repository;

import com.hcas.enrollmentreport.model.EnrollmentError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentErrorRepository extends JpaRepository<EnrollmentError, Long> {
}