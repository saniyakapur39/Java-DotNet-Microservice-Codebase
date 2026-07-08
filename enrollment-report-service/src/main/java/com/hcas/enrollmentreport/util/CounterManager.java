package com.hcas.enrollmentreport.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterManager {
    private int totalErrors = 0;
    private int criticalCnt = 0;
    private int warningCnt = 0;
    private int infoCnt = 0;
    private int addErrCnt = 0;
    private int chgErrCnt = 0;
    private int termErrCnt = 0;
    private int reinErrCnt = 0;
    private int lineCount = 99;
    private int pageCount = 0;
    private int maxLines = 55;
    private int recordsRead = 0;
}