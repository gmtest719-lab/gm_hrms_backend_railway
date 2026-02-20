package com.gm.hrms.repository;

import com.gm.hrms.entity.EmployeeAuth;
import com.gm.hrms.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByEmployeeAuth(EmployeeAuth auth);
}

