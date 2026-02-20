package com.gm.hrms.service.impl;

import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.config.JwtService;
import com.gm.hrms.dto.request.ChangePasswordRequestDTO;
import com.gm.hrms.dto.request.LoginRequestDTO;
import com.gm.hrms.dto.response.LoginResponseDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeAuth;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.LoginMapper;
import com.gm.hrms.repository.EmployeeAuthRepository;
import com.gm.hrms.service.AuthService;
import com.gm.hrms.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmployeeAuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    // ================= CREATE AUTH =================

    @Override
    public void createAuthForEmployee(Employee employee, String rawPassword) {

        EmployeeAuth auth = new EmployeeAuth();

        auth.setEmployee(employee);
        auth.setUsername(employee.getContact().getOfficeEmail());
        auth.setPasswordHash(passwordEncoder.encode(rawPassword));
        auth.setActive(true);
        auth.setAccountLocked(false);
        auth.setFailedLoginAttempts(0);
        auth.setIsLoggedIn(false);

        authRepository.save(auth);
    }

    // ================= LOGIN =================

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();

        String username = user.getUsername();

        EmployeeAuth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        // Generate tokens
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);

        // Save refresh token in DB
        refreshTokenService.deleteByAuth(auth); // remove old
        refreshTokenService.create(auth, refreshToken);

        // Update login flags
        auth.setIsLoggedIn(true);
        auth.setLastLoginAt(LocalDateTime.now());
        authRepository.save(auth);

        return LoginMapper.toResponse(accessToken, refreshToken, auth);
    }

    // ================= REFRESH TOKEN =================

    @Transactional
    @Override
    public LoginResponseDTO refreshToken(String refreshToken) {

        // Verify from DB + expiry
        var refreshEntity = refreshTokenService.verify(refreshToken);

        EmployeeAuth auth = refreshEntity.getEmployeeAuth();

        if(Boolean.FALSE.equals(auth.getIsLoggedIn())){
            throw new RuntimeException("User logged out. Please login again");
        }

        String username = auth.getUsername();

        // Generate new tokens
        String newAccessToken = jwtService.generateToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        // Rotate refresh token
        refreshTokenService.deleteByAuth(auth);
        refreshTokenService.create(auth, newRefreshToken);

        return LoginMapper.toResponse(newAccessToken, newRefreshToken, auth);
    }

    // ================= LOGOUT =================

    @Transactional
    @Override
    public void logout(String refreshToken) {

        var refreshEntity = refreshTokenService.verify(refreshToken);

        EmployeeAuth auth = refreshEntity.getEmployeeAuth();

        auth.setIsLoggedIn(false);
        authRepository.save(auth);

        // Delete refresh token
        refreshTokenService.deleteByAuth(auth);
    }


    @Transactional
    @Override
    public void changePassword(ChangePasswordRequestDTO request) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        EmployeeAuth auth = authRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), auth.getPasswordHash())) {
            throw new InvalidRequestException("Old password is incorrect");
        }

        // Validate new password
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new InvalidRequestException("Password must be at least 6 characters");
        }

        // Prevent same password reuse (optional but good practice)
        if (passwordEncoder.matches(request.getNewPassword(), auth.getPasswordHash())) {
            throw new InvalidRequestException("New password cannot be same as old password");
        }

        // Update password
        auth.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        authRepository.save(auth);

        // Invalidate old refresh tokens
        refreshTokenService.deleteByAuth(auth);
    }



}
