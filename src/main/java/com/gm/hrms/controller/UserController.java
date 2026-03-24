package com.gm.hrms.controller;

import com.gm.hrms.dto.request.*;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<Object>> create(

            @RequestParam("personalInformation") String personalInformationJson,
            @RequestParam(value = "intern", required = false) String internJson,
            @RequestParam(value = "employee", required = false) String employeeJson,
            @RequestParam(value = "trainee", required = false) String traineeJson,
            @RequestParam("profileImage") MultipartFile profileImage,
            @RequestParam(required = false) Map<String, MultipartFile> documents,
            @RequestParam(required = false) Map<String, String> reasons

    ) throws Exception {

        Object response = userService.create(
                personalInformationJson,
                internJson,
                employeeJson,
                traineeJson,
                profileImage,
                documents,
                reasons
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("User created successfully")
                        .data(response)
                        .build()
        );
    }
}