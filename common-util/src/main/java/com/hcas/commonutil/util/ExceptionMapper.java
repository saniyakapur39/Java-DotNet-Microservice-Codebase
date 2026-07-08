package com.hcas.commonutil.util;

public class ExceptionMapper {
    public static String map(Exception e) {
        if (e instanceof java.io.FileNotFoundException) {
            return ErrorCodes.FILE_NOT_FOUND;
        } else if (e instanceof java.io.IOException) {
            return ErrorCodes.FILE_OPEN_ERROR;
        } else {
            return ErrorCodes.FORMAT_ERROR;
        }
    }
}