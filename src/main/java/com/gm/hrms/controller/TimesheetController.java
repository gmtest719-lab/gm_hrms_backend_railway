package com.gm.hrms.controller;

import com.gm.hrms.dto.request.TimesheetRequestDTO;
import com.gm.hrms.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
public class TimesheetController {

    private final TimesheetService service;

    @PostMapping("/{employeeId}")
    public ResponseEntity<?> create(
            @PathVariable Long employeeId,
            @RequestBody TimesheetRequestDTO dto){

        return ResponseEntity.ok(service.create(employeeId, dto));
    }

    @PatchMapping("/submit/{timesheetId}")
    public ResponseEntity<?> submit(@PathVariable Long timesheetId){
        return ResponseEntity.ok(service.submit(timesheetId));
    }

    @PatchMapping("/approve/{timesheetId}")
    public ResponseEntity<?> approve(@PathVariable Long timesheetId){
        return ResponseEntity.ok(service.approve(timesheetId));
    }

    @PatchMapping("/reject/{timesheetId}")
    public ResponseEntity<?> reject(@PathVariable Long timesheetId){
        return ResponseEntity.ok(service.reject(timesheetId));
    }

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getByEmployee(@PathVariable Long employeeId){
        return ResponseEntity.ok(service.getByEmployee(employeeId));
    }
}
