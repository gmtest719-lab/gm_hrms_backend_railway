package com.gm.hrms.exception;

public class EmployeeReportException extends RuntimeException {

    public EmployeeReportException(String message) {
        super(message);
    }

    public EmployeeReportException(String message, Throwable cause) {
        super(message, cause);
    }
}