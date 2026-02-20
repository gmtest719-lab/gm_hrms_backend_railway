package com.gm.hrms.service;

import com.gm.hrms.entity.EmployeeAuth;
import com.gm.hrms.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken create(EmployeeAuth auth, String token);

    RefreshToken verify(String token);

    void deleteByAuth(EmployeeAuth auth);
}

