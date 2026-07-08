package com.hcas.enrollmentreport.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EnrollmentErrorDto {
    private String memberId;
    private String subscriberId;
    private String groupId;
    private String errorCode;
    private String errorDescription;
    private String transactionType;
    private String severityFlag;
    private String fieldName;
    private String fieldValue;
    private String processDate;
    private String sourceFileId;
}