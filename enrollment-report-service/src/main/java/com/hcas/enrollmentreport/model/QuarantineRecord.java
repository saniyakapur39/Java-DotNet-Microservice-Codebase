package com.hcas.enrollmentreport.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stg_enrollment_error_quarantine")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class QuarantineRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, nullable = false)
    private String sourceFile;

    @Column(columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String sourceRecordData;

    @Column(length = 64, nullable = false)
    private String failureReasonCode;

    @Column(nullable = false)
    private LocalDateTime failureTimestamp;

    @Column(length = 32, nullable = false)
    private String resolutionStatus;

    @Column(length = 64)
    private String resolvedBy;

    private LocalDateTime resolvedAt;
}