package com.gm.hrms.config;

import com.gm.hrms.entity.EmployeeAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final EmployeeAuth employeeAuth;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        String roleName = employeeAuth
                .getEmployee()
                .getRole()
                .name();

        return List.of(() -> "ROLE_" + roleName);
    }

    @Override
    public String getPassword() {
        return employeeAuth.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return employeeAuth.getUsername();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !employeeAuth.getAccountLocked();
    }

    @Override
    public boolean isEnabled() {
        return employeeAuth.getActive();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override public boolean isCredentialsNonExpired() { return true; }
}

