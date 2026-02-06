package com.gm.hrms.controller;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.EmployeeService;
import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;
    private final ObjectMapper objectMapper;

    // ⭐ CREATE EMPLOYEE (Multipart)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> createEmployee(

            @RequestPart("employee") String employeeJson,
            @RequestPart(value = "documents", required = false) List<MultipartFile> documents

    ) throws Exception {

        EmployeeRequestDTO dto =
                objectMapper.readValue(employeeJson, EmployeeRequestDTO.class);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Employee created successfully")
                        .data(service.create(dto, documents))
                        .build()
        );
    }

    // ⭐ UPDATE EMPLOYEE (Multipart Support Added)
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> updateEmployee(

            @PathVariable Long id,
            @RequestPart("employee") String employeeJson,
            @RequestPart(value = "documents", required = false) List<MultipartFile> documents

    ) throws Exception {

        EmployeeUpdateDTO dto =
                objectMapper.readValue(employeeJson, EmployeeUpdateDTO.class);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Employee updated successfully")
                        .data(service.update(id, dto, documents))
                        .build()
        );
    }

    // ⭐ GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Employee fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // ⭐ GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Employees fetched successfully")
                        .data(service.getAll())
                        .build()
        );
    }

    // ⭐ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id){

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Employee deleted successfully")
                        .build()
        );
    }
}
