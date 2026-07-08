package com.hcas.enrollmentreport.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandler {
    public static void handle(Exception e, String context) {
        log.error("Exception in {}: {}", context, e.getMessage(), e);
    }
}