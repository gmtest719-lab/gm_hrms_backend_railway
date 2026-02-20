package com.gm.hrms.config;

import com.gm.hrms.entity.EmployeeAuth;
import com.gm.hrms.repository.EmployeeAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeAuthRepository employeeAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        EmployeeAuth auth = employeeAuthRepository.findByUsernameWithEmployee(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(auth);
    }
}
