package com.gm.hrms.service;

public interface EmailService {

    void sendCredentials(String to, String name, String password);

    void sendDownloadApprovalEmail(String toEmail, String employeeName,
                                   String monthYear, String plainPassword);

    void sendDownloadRejectionEmail(String toEmail, String employeeName,
                                    String monthYear, String rejectionReason);

    String sendTimesheetSummaryEmail(String toEmail, String employeeName,
                                     String workDate, String entries,
                                     String totalTime);

    void sendTimesheetUpdateEmail(String toEmail, String employeeName,
                                  String workDate, String entries,
                                  String totalTime, String originalMessageId);

    void sendAccessRequestNotification(String toEmail, String employeeName,
                                       String requestedDate, String accessType,
                                       String reason);
}