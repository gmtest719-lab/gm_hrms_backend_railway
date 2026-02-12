package com.gm.hrms.service;

import com.gm.hrms.entity.Employee;

public interface AuthService {

    void createAuthForEmployee(Employee employee, String rawPassword);
}

