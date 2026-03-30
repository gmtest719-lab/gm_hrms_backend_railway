package com.gm.hrms.service.impl;

import com.gm.hrms.config.JwtService;
import com.gm.hrms.dto.request.ChangePasswordRequestDTO;
import com.gm.hrms.dto.request.LoginRequestDTO;
import com.gm.hrms.dto.response.LoginResponseDTO;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.entity.UserAuth;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.LoginMapper;
import com.gm.hrms.repository.UserAuthRepository;
import com.gm.hrms.service.AuthService;
import com.gm.hrms.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserAuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    // ================= CREATE AUTH =================

    @Override
    public void createAuthForPerson(PersonalInformation person,
                                    RoleType role,
                                    String rawPassword) {

        String username = username(person);

        if (authRepository.existsByUsername(username)) {
            throw new InvalidRequestException("Username already exists");
        }

        UserAuth auth = UserAuth.builder()
                .personalInformation(person)
                .username(username)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .active(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .isLoggedIn(false)
                .build();

        authRepository.save(auth);
    }

    @Override
    public boolean existsByPerson(PersonalInformation person) {
        return authRepository.existsByPersonalInformation(person);
    }

    // ================= LOGIN =================

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserAuth auth = authRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!auth.getActive()) {
            throw new InvalidRequestException("Account inactive");
        }

        String accessToken = jwtService.generateToken(
                auth.getUsername(),
                auth.getRole().name()
        );

        String refreshToken = jwtService.generateRefreshToken(auth.getUsername());

        refreshTokenService.deleteByAuth(auth);
        refreshTokenService.create(auth, refreshToken);

        auth.setIsLoggedIn(true);
        auth.setLastLoginAt(LocalDateTime.now());

        return LoginMapper.toResponse(accessToken, refreshToken, auth);
    }

    // ================= REFRESH =================

    @Override
    public LoginResponseDTO refreshToken(String refreshToken) {

        var refreshEntity = refreshTokenService.verify(refreshToken);
        UserAuth auth = refreshEntity.getUserAuth();

        if (!auth.getIsLoggedIn()) {
            throw new InvalidRequestException("User logged out");
        }

        String newAccessToken = jwtService.generateToken(
                auth.getUsername(),
                auth.getRole().name()
        );

        String newRefreshToken = jwtService.generateRefreshToken(auth.getUsername());

        refreshTokenService.deleteByAuth(auth);
        refreshTokenService.create(auth, newRefreshToken);

        return LoginMapper.toResponse(newAccessToken, newRefreshToken, auth);
    }

    // ================= LOGOUT =================

    @Override
    public void logout(String refreshToken) {

        var refreshEntity = refreshTokenService.verify(refreshToken);
        UserAuth auth = refreshEntity.getUserAuth();

        auth.setIsLoggedIn(false);
        refreshTokenService.deleteByAuth(auth);
    }

    // ================= CHANGE PASSWORD =================

    @Override
    public void changePassword(ChangePasswordRequestDTO request) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        UserAuth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), auth.getPasswordHash())) {
            throw new InvalidRequestException("Old password incorrect");
        }

        if (request.getNewPassword().length() < 6) {
            throw new InvalidRequestException("Password must be at least 6 characters");
        }

        auth.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        refreshTokenService.deleteByAuth(auth);
    }

    // ================= USERNAME RESOLVER =================

    private String username(PersonalInformation person) {

        if (person.getContact() != null &&
                person.getContact().getOfficeEmail() != null &&
                !person.getContact().getOfficeEmail().isBlank()) {

            return person.getContact().getOfficeEmail();
        }

        return person.getContact().getPersonalEmail();
    }
}