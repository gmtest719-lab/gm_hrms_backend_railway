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
}
