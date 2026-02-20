package com.gm.hrms.service.impl;

import com.gm.hrms.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

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
}
