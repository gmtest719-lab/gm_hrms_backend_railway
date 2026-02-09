package com.gm.hrms.service.impl;

import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeAuth;
import com.gm.hrms.repository.EmployeeAuthRepository;
import com.gm.hrms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmployeeAuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createAuthForEmployee(Employee employee, String rawPassword) {

        EmployeeAuth auth = new EmployeeAuth();

        auth.setEmployee(employee);
        auth.setUsername(employee.getContact().getOfficeEmail());
        auth.setPasswordHash(passwordEncoder.encode(rawPassword));
        auth.setActive(true);
        auth.setFailedLoginAttempts(0);
        auth.setAccountLocked(false);

        authRepository.save(auth);
    }


}
