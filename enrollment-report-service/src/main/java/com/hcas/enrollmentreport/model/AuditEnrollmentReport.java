package com.hcas.enrollmentreport.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_enrollment_report")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AuditEnrollmentReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID jobId;

    @Column(nullable = false)
    private LocalDateTime runStart;

    @Column(nullable = false)
    private LocalDateTime runEnd;

    @Column(nullable = false)
    private int recordsRead;

    @Column(nullable = false)
    private int errorsReported;

    @Column(nullable = false)
    private int pagesGenerated;

    @Column(length = 16, nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String failureReason;
}