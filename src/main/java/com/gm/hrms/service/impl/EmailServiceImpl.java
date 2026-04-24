package com.gm.hrms.service.impl;

import com.gm.hrms.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${hrms.company.name:G.M. Technosys}")
    private String companyName;

    @Value("${hrms.timesheet.summary.email:hr@company.com}")
    private String timesheetSummaryEmail;

    @Override
    public void sendCredentials(String to, String name, String password) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Your GM HRMS Login Credentials");

        message.setText(
                "Hello " + name + ",\n\n" +
                        "Your account has been created.\n\n" +
                        "Username: " + to + "\n" +
                        "Password: " + password + "\n\n" +
                        "Please login and change your password.\n\n" +
                        "Thanks,\nHR Team"
        );

        mailSender.send(message);
    }

    // ── Approval Email ────────────────────────────────────────────────────

    @Async
    @Override
    public void sendDownloadApprovalEmail(String toEmail,
                                          String employeeName,
                                          String monthYear,
                                          String plainPassword) {
        String subject = companyName + " | Salary Slip Download Approved – " + monthYear;
        String body    = buildApprovalBody(employeeName, monthYear, plainPassword);
        sendHtml(toEmail, subject, body);
    }

    // ── Rejection Email ───────────────────────────────────────────────────

    @Async
    @Override
    public void sendDownloadRejectionEmail(String toEmail,
                                           String employeeName,
                                           String monthYear,
                                           String rejectionReason) {
        String subject = companyName + " | Salary Slip Download Request Rejected – " + monthYear;
        String body    = buildRejectionBody(employeeName, monthYear, rejectionReason);
        sendHtml(toEmail, subject, body);
    }

    @Async
    @Override
    public String sendTimesheetSummaryEmail(String toEmail, String employeeName,
                                            String workDate, String entries,
                                            String totalTime) {
        String messageId = "<ts-" + System.currentTimeMillis() + "@hrms.gm>";
        String subject   = companyName + " | Timesheet Submitted – " + workDate + " – " + employeeName;
        String body      = buildTimesheetEmailBody("Timesheet Submitted", employeeName,
                workDate, entries, totalTime, false);
        sendHtmlWithMessageId(toEmail, subject, body, messageId, null);
        return messageId;
    }

// ── Timesheet Update Email (continues the thread) ────────────────────────

    @Async
    @Override
    public void sendTimesheetUpdateEmail(String toEmail, String employeeName,
                                         String workDate, String entries,
                                         String totalTime, String originalMessageId) {
        String newMessageId = "<ts-upd-" + System.currentTimeMillis() + "@hrms.gm>";
        String subject      = "Re: " + companyName + " | Timesheet Submitted – " + workDate + " – " + employeeName;
        String body         = buildTimesheetEmailBody("Timesheet Updated", employeeName,
                workDate, entries, totalTime, true);
        sendHtmlWithMessageId(toEmail, subject, body, newMessageId, originalMessageId);
    }

// ── Access Request Notification ───────────────────────────────────────────

    @Async
    @Override
    public void sendAccessRequestNotification(String toEmail, String employeeName,
                                              String requestedDate, String accessType,
                                              String reason) {
        String subject = companyName + " | Timesheet Access Request – " + employeeName;
        String body = """
            <!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"/>
            <style>
              body{font-family:Arial,sans-serif;color:#333;}
              .wrap{max-width:600px;margin:30px auto;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden;}
              .hdr{background:#f57c00;padding:24px 32px;}
              .hdr h2{color:#fff;margin:0;font-size:20px;}
              .body{padding:28px 32px;}
              .ftr{background:#f9f9f9;padding:16px 32px;font-size:12px;color:#aaa;text-align:center;}
            </style></head><body>
            <div class="wrap">
              <div class="hdr"><h2>Timesheet Access Request</h2></div>
              <div class="body">
                <p><strong>%s</strong> has requested access to %s their timesheet.</p>
                <ul>
                  <li><strong>Date:</strong> %s</li>
                  <li><strong>Type:</strong> %s</li>
                  <li><strong>Reason:</strong> %s</li>
                </ul>
                <p>Please log in to HRMS and review this request.</p>
                <p>Regards,<br/><strong>HRMS System — %s</strong></p>
              </div>
              <div class="ftr">This is an automated message. Please do not reply.</div>
            </div></body></html>
            """.formatted(employeeName,
                "EDIT_OLD".equals(accessType) ? "edit an old" : "add extra hours to",
                requestedDate, accessType, escapeHtml(reason), companyName);
        sendHtml(toEmail, subject, body);
    }


    // ── Core send helper ──────────────────────────────────────────────────

    private void sendHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);          // true = html
            mailSender.send(message);
            log.info("Email sent successfully to [{}] | subject: {}", to, subject);
        } catch (MailException | jakarta.mail.MessagingException ex) {
            // Log and swallow — email failures must not break the business flow
            log.error("Failed to send email to [{}]: {}", to, ex.getMessage(), ex);
        }
    }

    // ── HTML body builders ────────────────────────────────────────────────

    private String buildApprovalBody(String name, String monthYear, String password) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <style>
                    body  { font-family: Arial, sans-serif; color: #333; margin: 0; padding: 0; }
                    .wrap { max-width: 600px; margin: 30px auto; border: 1px solid #e0e0e0;
                            border-radius: 8px; overflow: hidden; }
                    .hdr  { background: #1a73e8; padding: 24px 32px; }
                    .hdr h2 { color: #fff; margin: 0; font-size: 20px; }
                    .body { padding: 28px 32px; }
                    .pwd-box { background: #f1f8ff; border: 1px dashed #1a73e8;
                               border-radius: 6px; padding: 16px 24px; margin: 20px 0;
                               text-align: center; }
                    .pwd  { font-size: 26px; font-weight: bold; letter-spacing: 4px;
                            color: #1a73e8; }
                    .note { font-size: 12px; color: #888; margin-top: 8px; }
                    .ftr  { background: #f9f9f9; padding: 16px 32px;
                            font-size: 12px; color: #aaa; text-align: center; }
                  </style>
                </head>
                <body>
                <div class="wrap">
                  <div class="hdr"><h2>%s — Salary Slip Download Approved</h2></div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your request to download the salary slip for
                       <strong>%s</strong> has been <strong style="color:#1a73e8">approved</strong>
                       by the HR/Admin team.</p>
                    <p>Use the one-time password below to unlock your download:</p>
                    <div class="pwd-box">
                      <div class="pwd">%s</div>
                      <div class="note">This password is valid for a single download only.
                                        Do not share it with anyone.</div>
                    </div>
                    <p>Steps:</p>
                    <ol>
                      <li>Log in to the HRMS portal.</li>
                      <li>Navigate to <em>Payroll → My Salary Slips</em>.</li>
                      <li>Select <strong>%s</strong> and click <em>Download</em>.</li>
                      <li>Enter the password above when prompted.</li>
                    </ol>
                    <p>If you did not raise this request, please contact HR immediately.</p>
                    <p>Regards,<br/><strong>HR Team — %s</strong></p>
                  </div>
                  <div class="ftr">This is an automated message. Please do not reply.</div>
                </div>
                </body>
                </html>
                """.formatted(companyName, name, monthYear, password,
                monthYear, companyName);
    }

    private String buildRejectionBody(String name, String monthYear, String reason) {
        String reasonHtml = (reason != null && !reason.isBlank())
                ? "<p><strong>Reason:</strong> " + escapeHtml(reason) + "</p>"
                : "";
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <style>
                    body  { font-family: Arial, sans-serif; color: #333; margin: 0; padding: 0; }
                    .wrap { max-width: 600px; margin: 30px auto; border: 1px solid #e0e0e0;
                            border-radius: 8px; overflow: hidden; }
                    .hdr  { background: #d93025; padding: 24px 32px; }
                    .hdr h2 { color: #fff; margin: 0; font-size: 20px; }
                    .body { padding: 28px 32px; }
                    .ftr  { background: #f9f9f9; padding: 16px 32px;
                            font-size: 12px; color: #aaa; text-align: center; }
                  </style>
                </head>
                <body>
                <div class="wrap">
                  <div class="hdr"><h2>%s — Salary Slip Download Request Rejected</h2></div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>We regret to inform you that your request to download the salary slip
                       for <strong>%s</strong> has been
                       <strong style="color:#d93025">rejected</strong>.</p>
                    %s
                    <p>If you believe this is an error, please contact your HR representative.</p>
                    <p>Regards,<br/><strong>HR Team — %s</strong></p>
                  </div>
                  <div class="ftr">This is an automated message. Please do not reply.</div>
                </div>
                </body>
                </html>
                """.formatted(companyName, name, monthYear, reasonHtml, companyName);
    }

    /** Minimal HTML escaping to avoid XSS from rejection reason text. */
    private static String escapeHtml(String text) {
        return text
                .replace("&",  "&amp;")
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;")
                .replace("'",  "&#x27;");
    }

    // ── Core send with custom Message-ID / In-Reply-To ───────────────────────

    private void sendHtmlWithMessageId(String to, String subject, String htmlBody,
                                       String messageId, String inReplyTo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            // Threading headers
            message.setHeader("Message-ID", messageId);
            if (inReplyTo != null) {
                message.setHeader("In-Reply-To", inReplyTo);
                message.setHeader("References",  inReplyTo);
            }

            mailSender.send(message);
            log.info("Email sent [{}] | subject: {}", to, subject);
        } catch (MailException | jakarta.mail.MessagingException ex) {
            log.error("Failed to send email to [{}]: {}", to, ex.getMessage(), ex);
        }
    }

// ── Timesheet HTML body builder ───────────────────────────────────────────

    private String buildTimesheetEmailBody(String title, String name,
                                           String workDate, String entriesHtml,
                                           String totalTime, boolean isUpdate) {
        String headerColor = isUpdate ? "#6a1b9a" : "#1a73e8";
        return """
            <!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"/>
            <style>
              body{font-family:Arial,sans-serif;color:#333;margin:0;padding:0;}
              .wrap{max-width:640px;margin:30px auto;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden;}
              .hdr{background:%s;padding:24px 32px;}
              .hdr h2{color:#fff;margin:0;font-size:20px;}
              .body{padding:28px 32px;}
              table{width:100%%;border-collapse:collapse;margin:16px 0;}
              th{background:#f5f5f5;padding:10px 12px;text-align:left;font-size:13px;border-bottom:2px solid #e0e0e0;}
              td{padding:10px 12px;font-size:13px;border-bottom:1px solid #f0f0f0;}
              .total{font-weight:bold;font-size:14px;text-align:right;margin-top:8px;}
              .ftr{background:#f9f9f9;padding:16px 32px;font-size:12px;color:#aaa;text-align:center;}
            </style></head><body>
            <div class="wrap">
              <div class="hdr"><h2>%s</h2></div>
              <div class="body">
                <p>Dear HR,</p>
                <p><strong>%s</strong> has %s their timesheet for <strong>%s</strong>.</p>
                <table>
                  <thead><tr><th>Project</th><th>Task</th><th>Time</th><th>Description</th></tr></thead>
                  <tbody>%s</tbody>
                </table>
                <p class="total">Total: %s hrs</p>
                <p>Regards,<br/><strong>HRMS System</strong></p>
              </div>
              <div class="ftr">This is an automated message. Please do not reply.</div>
            </div></body></html>
            """.formatted(headerColor, title, name,
                isUpdate ? "updated" : "submitted",
                workDate, entriesHtml, totalTime);
    }
}
