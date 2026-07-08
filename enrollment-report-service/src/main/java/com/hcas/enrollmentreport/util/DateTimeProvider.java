package com.hcas.enrollmentreport.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeProvider {
    public static LocalDate nowDate() {
        return LocalDate.now();
    }
    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now();
    }
    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}