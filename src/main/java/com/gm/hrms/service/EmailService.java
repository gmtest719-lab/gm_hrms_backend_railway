package com.gm.hrms.service;

public interface EmailService {
    void sendCredentials(String to, String name, String password);
}

