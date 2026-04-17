package com.gm.hrms.service;

public interface EmailService {
    void sendCredentials(String to, String name, String password);

    /**
     * Sends the salary-slip download approval email to an employee.
     *
     * @param toEmail       Employee's email address
     * @param employeeName  Full name of the employee (for salutation)
     * @param monthYear     Human-readable period, e.g. "April 2025"
     * @param plainPassword The raw (un-hashed) one-time download password
     */
    void sendDownloadApprovalEmail(String toEmail,
                                   String employeeName,
                                   String monthYear,
                                   String plainPassword);

    /**
     * Sends the salary-slip download rejection email to an employee.
     *
     * @param toEmail         Employee's email address
     * @param employeeName    Full name of the employee (for salutation)
     * @param monthYear       Human-readable period, e.g. "April 2025"
     * @param rejectionReason Optional reason provided by the admin (may be null)
     */
    void sendDownloadRejectionEmail(String toEmail,
                                    String employeeName,
                                    String monthYear,
                                    String rejectionReason);
}

