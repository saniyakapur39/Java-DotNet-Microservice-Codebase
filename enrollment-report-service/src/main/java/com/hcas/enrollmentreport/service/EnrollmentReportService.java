package com.hcas.enrollmentreport.service;

import com.hcas.enrollmentreport.config.EnrollmentReportConfig;
import com.hcas.enrollmentreport.dto.ReportRunDto;
import com.hcas.enrollmentreport.model.*;
import com.hcas.enrollmentreport.repository.*;
import com.hcas.enrollmentreport.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentReportService {
    private final EnrollmentReportConfig config;
    private final EnrollmentErrorRepository errorRepository;
    private final AuditEnrollmentReportRepository auditRepository;
    private final QuarantineRepository quarantineRepository;

    @Transactional
    public ReportRunDto runReport() {
        UUID jobId = UUID.randomUUID();
        LocalDateTime runStart = DateTimeProvider.nowDateTime();
        CounterManager counters = new CounterManager();
        counters.setMaxLines(config.getMaxLinesPerPage());

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(config.getInputFilePath()));
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(config.getOutputFilePath()))) {

            // Write headers for first page
            writePageHeaders(writer, counters);

            String line;
            while ((line = reader.readLine()) != null) {
                counters.setRecordsRead(counters.getRecordsRead() + 1);
                EnrollmentError error = parseLineToError(line, counters, runStart);
                if (error == null) continue; // Invalid, already quarantined

                tallyCounters(error, counters);
                if (counters.getLineCount() >= counters.getMaxLines()) {
                    writePageHeaders(writer, counters);
                }
                writer.write(formatDetailLine(error));
                writer.newLine();
                counters.setLineCount(counters.getLineCount() + 1);
            }

            writeSummary(writer, counters);
            writer.flush();

            LocalDateTime runEnd = DateTimeProvider.nowDateTime();
            auditRepository.save(AuditEnrollmentReport.builder()
                .jobId(jobId)
                .runStart(runStart)
                .runEnd(runEnd)
                .recordsRead(counters.getRecordsRead())
                .errorsReported(counters.getTotalErrors())
                .pagesGenerated(counters.getPageCount())
                .status("SUCCESS")
                .failureReason(null)
                .build());

            return ReportRunDto.builder()
                    .jobId(jobId.toString())
                    .status("SUCCESS")
                    .reportPath(config.getOutputFilePath())
                    .build();
        } catch (IOException e) {
            ExceptionHandler.handle(e, "EnrollmentReportService.runReport");
            auditRepository.save(AuditEnrollmentReport.builder()
                .jobId(jobId)
                .runStart(runStart)
                .runEnd(DateTimeProvider.nowDateTime())
                .recordsRead(counters.getRecordsRead())
                .errorsReported(counters.getTotalErrors())
                .pagesGenerated(counters.getPageCount())
                .status("FAILED")
                .failureReason(e.getMessage())
                .build());
            throw new com.hcas.enrollmentreport.exception.EnrollmentReportException("Failed to process report: " + e.getMessage());
        }
    }

    private EnrollmentError parseLineToError(String line, CounterManager counters, LocalDateTime loadedAt) {
        try {
            // Assuming fixed-width fields as per COBOL layout
            String memberId = line.substring(0, 12).trim();
            String subscriberId = line.substring(12, 24).trim();
            String groupId = line.substring(24, 34).trim();
            String errorCode = line.substring(34, 38).trim();
            String errorDesc = line.substring(38, 98).trim();
            String txnType = line.substring(98, 100).trim();
            String severity = line.substring(100, 101).trim();
            String fieldName = line.substring(101, 131).trim();
            String fieldValue = line.substring(131, 161).trim();
            String processDateStr = line.substring(161, 169).trim();
            String sourceFileId = line.substring(169, 189).trim();

            // Validation (as per Data Migration Strategy)
            if (memberId.isEmpty() || subscriberId.isEmpty() || groupId.isEmpty() || errorCode.isEmpty() ||
                    errorDesc.isEmpty() || processDateStr.isEmpty() ||
                    !(severity.equals("C") || severity.equals("W") || severity.equals("I")) ||
                    !(txnType.equals("AD") || txnType.equals("CH") || txnType.equals("TM") || txnType.equals("RI"))) {
                quarantineRepository.save(QuarantineRecord.builder()
                        .sourceFile(config.getInputFilePath())
                        .sourceRecordData(line)
                        .failureReasonCode("FIELD_VALIDATION")
                        .failureTimestamp(DateTimeProvider.nowDateTime())
                        .resolutionStatus("UNRESOLVED")
                        .build());
                return null;
            }

            EnrollmentError error = EnrollmentError.builder()
                    .memberId(memberId)
                    .subscriberId(subscriberId)
                    .groupId(groupId)
                    .errorCode(errorCode)
                    .errorDescription(errorDesc)
                    .transactionType(txnType)
                    .severityFlag(severity)
                    .fieldName(fieldName)
                    .fieldValue(fieldValue)
                    .processDate(LocalDate.parse(processDateStr, java.time.format.DateTimeFormatter.BASIC_ISO_DATE))
                    .sourceFileId(sourceFileId)
                    .loadedAt(loadedAt)
                    .build();
            errorRepository.save(error);
            counters.setTotalErrors(counters.getTotalErrors() + 1);
            return error;
        } catch (Exception e) {
            quarantineRepository.save(QuarantineRecord.builder()
                    .sourceFile(config.getInputFilePath())
                    .sourceRecordData(line)
                    .failureReasonCode("PARSE_ERROR")
                    .failureTimestamp(DateTimeProvider.nowDateTime())
                    .resolutionStatus("UNRESOLVED")
                    .build());
            return null;
        }
    }

    private void tallyCounters(EnrollmentError error, CounterManager counters) {
        switch (error.getSeverityFlag()) {
            case "C": counters.setCriticalCnt(counters.getCriticalCnt() + 1); break;
            case "W": counters.setWarningCnt(counters.getWarningCnt() + 1); break;
            default: counters.setInfoCnt(counters.getInfoCnt() + 1);
        }
        switch (error.getTransactionType()) {
            case "AD": counters.setAddErrCnt(counters.getAddErrCnt() + 1); break;
            case "CH": counters.setChgErrCnt(counters.getChgErrCnt() + 1); break;
            case "TM": counters.setTermErrCnt(counters.getTermErrCnt() + 1); break;
            case "RI": counters.setReinErrCnt(counters.getReinErrCnt() + 1); break;
        }
    }

    private void writePageHeaders(BufferedWriter writer, CounterManager counters) throws IOException {
        counters.setPageCount(counters.getPageCount() + 1);
        counters.setLineCount(4); // Header lines
        String date = DateTimeProvider.formatDate(DateTimeProvider.nowDate());
        String header1 = String.format(" NATIONAL HEALTH PARTNERS INC           DAILY ENROLLMENT ERROR REPT   %10s    PAGE %3d", date, counters.getPageCount());
        String header2 = " MEMBER ID   SUBSCRIBER   GROUP     CODE TXN SEV FIELD NAME                    ERROR DESCRIPTION";
        String header3 = " " + "-".repeat(132);
        writer.write(header1); writer.newLine();
        writer.write(header2); writer.newLine();
        writer.write(header3); writer.newLine();
    }

    private String formatDetailLine(EnrollmentError error) {
        String severityDisplay;
        switch (error.getSeverityFlag()) {
            case "C": severityDisplay = "***"; break;
            case "W": severityDisplay = "WRN"; break;
            default: severityDisplay = "INF";
        }
        // Fixed-width formatting
        return String.format(" %-12s %-12s %-10s %-4s %-3s %-3s %-30s %-50s",
                error.getMemberId(),
                error.getSubscriberId(),
                error.getGroupId(),
                error.getErrorCode(),
                error.getTransactionType(),
                severityDisplay,
                error.getFieldName(),
                error.getErrorDescription());
    }

    private void writeSummary(BufferedWriter writer, CounterManager counters) throws IOException {
        writer.newLine(); writer.newLine(); writer.newLine();
        writer.write(" ========== PROCESSING SUMMARY =========="); writer.newLine();
        writer.newLine();
        writer.write(String.format("     TOTAL ERRORS PROCESSED: %7d", counters.getTotalErrors())); writer.newLine();
        writer.write(String.format("     CRITICAL ERRORS:        %7d", counters.getCriticalCnt())); writer.newLine();
        writer.write(String.format("     WARNINGS:               %7d", counters.getWarningCnt())); writer.newLine();
        writer.write(String.format("     INFORMATIONAL:          %7d", counters.getInfoCnt())); writer.newLine();
        writer.newLine();
        writer.write(String.format("     ADD TRANSACTION ERRORS:     %7d", counters.getAddErrCnt())); writer.newLine();
        writer.write(String.format("     CHANGE TRANSACTION ERRORS:  %7d", counters.getChgErrCnt())); writer.newLine();
        writer.write(String.format("     TERM TRANSACTION ERRORS:    %7d", counters.getTermErrCnt())); writer.newLine();
        writer.write(String.format("     REINSTATE TXN ERRORS:       %7d", counters.getReinErrCnt())); writer.newLine();
    }
}