package com.gm.hrms.service.impl;

import com.gm.hrms.entity.EmployeeAuth;
import com.gm.hrms.entity.RefreshToken;
import com.gm.hrms.exception.TokenExpiredException;
import com.gm.hrms.exception.TokenNotFoundException;
import com.gm.hrms.repository.RefreshTokenRepository;
import com.gm.hrms.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;

    // ================= CREATE =================
    @Override
    public RefreshToken create(EmployeeAuth auth, String token) {

        RefreshToken refresh = RefreshToken.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .employeeAuth(auth)
                .build();

        return repository.save(refresh);
    }

    // ================= VERIFY =================
    @Override
    @Transactional
    public RefreshToken verify(String token) {

        RefreshToken refresh = repository.findByToken(token)
                .orElseThrow(() ->
                        new TokenNotFoundException("Refresh token not found"));

        if (refresh.getExpiryDate().isBefore(LocalDateTime.now())) {
            repository.delete(refresh);
            throw new TokenExpiredException("Refresh token expired");
        }

        return refresh;
    }

    // ================= DELETE =================
    @Override
    @Transactional
    public void deleteByAuth(EmployeeAuth auth) {
        repository.deleteByEmployeeAuth(auth);
    }
}
