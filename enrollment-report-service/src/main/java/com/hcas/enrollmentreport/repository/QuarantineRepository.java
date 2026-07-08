package com.hcas.enrollmentreport.repository;

import com.hcas.enrollmentreport.model.QuarantineRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuarantineRepository extends JpaRepository<QuarantineRecord, Long> {
}