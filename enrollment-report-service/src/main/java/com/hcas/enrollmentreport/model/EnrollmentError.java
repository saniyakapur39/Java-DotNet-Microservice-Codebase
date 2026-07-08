package com.hcas.enrollmentreport.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "enrollment_error")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class EnrollmentError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 12, nullable = false)
    private String memberId;

    @Column(length = 12, nullable = false)
    private String subscriberId;

    @Column(length = 10, nullable = false)
    private String groupId;

    @Column(length = 4, nullable = false)
    private String errorCode;

    @Column(length = 60, nullable = false)
    private String errorDescription;

    @Column(length = 2, nullable = false)
    private String transactionType;

    @Column(length = 1, nullable = false)
    private String severityFlag;

    @Column(length = 30, nullable = false)
    private String fieldName;

    @Column(length = 30)
    private String fieldValue;

    @Column(nullable = false)
    private LocalDate processDate;

    @Column(length = 20)
    private String sourceFileId;

    @Column(nullable = false)
    private java.time.LocalDateTime loadedAt;
}