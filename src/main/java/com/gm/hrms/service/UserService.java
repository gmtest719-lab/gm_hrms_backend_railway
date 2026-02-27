package com.gm.hrms.service;

import com.gm.hrms.dto.request.UserCreateRequestDTO;

public interface UserService {

    Object create(UserCreateRequestDTO dto);
}