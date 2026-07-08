package com.hcas.enrollmentreport.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EnrollmentReportException.class)
    public ResponseEntity<String> handleEnrollmentReportException(EnrollmentReportException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}