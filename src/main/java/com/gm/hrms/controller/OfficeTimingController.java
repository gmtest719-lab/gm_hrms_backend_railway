package com.gm.hrms.controller;

import com.gm.hrms.dto.request.OfficeTimingRequestDTO;
import com.gm.hrms.service.OfficeTimingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/office-timing")
@RequiredArgsConstructor
public class OfficeTimingController {

    private final OfficeTimingService service;

    @PostMapping
    public ResponseEntity<?> createOrUpdate(
            @RequestBody OfficeTimingRequestDTO dto){

        return ResponseEntity.ok(service.createOrUpdate(dto));
    }

    @GetMapping
    public ResponseEntity<?> get(){
        return ResponseEntity.ok(service.get());
    }
}

