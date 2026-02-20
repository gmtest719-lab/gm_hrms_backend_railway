package com.gm.hrms.service;

import com.gm.hrms.dto.request.ChangePasswordRequestDTO;
import com.gm.hrms.dto.request.LoginRequestDTO;
import com.gm.hrms.dto.response.LoginResponseDTO;
import com.gm.hrms.entity.Employee;

public interface AuthService {

    void createAuthForEmployee(Employee employee, String rawPassword);

    LoginResponseDTO login(LoginRequestDTO request);

    LoginResponseDTO refreshToken(String refreshToken);

    void logout(String refreshToken);

    void changePassword(ChangePasswordRequestDTO request);

}

