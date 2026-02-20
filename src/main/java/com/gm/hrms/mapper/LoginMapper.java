package com.gm.hrms.mapper;


import com.gm.hrms.dto.response.LoginResponseDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeAuth;

public class LoginMapper {

    public static LoginResponseDTO toResponse(
            String accessToken,
            String refreshToken,
            EmployeeAuth auth
    ) {

        Employee emp = auth.getEmployee();

        return LoginResponseDTO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(emp.getId())
                .username(auth.getUsername())
                .fullName(emp.getFirstName() + " " + emp.getLastName())
                .role(emp.getRole().name())
                .department(emp.getDepartment().getName())
                .active(auth.getActive())
                .build();
    }
}



