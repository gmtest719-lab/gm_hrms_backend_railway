package com.gm.hrms.controller;

import com.gm.hrms.dto.request.TimesheetRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
public class TimesheetController {

    private final TimesheetService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createOrUpdate(
            @RequestBody TimesheetRequestDTO request){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet saved")
                        .data(service.createOrUpdateTimesheet(request))
                        .build()
        );
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<?>> submit(@PathVariable Long id){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet submitted")
                        .data(service.submitTimesheet(id))
                        .build()
        );
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<?>> approve(@PathVariable Long id){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet approved")
                        .data(service.approveTimesheet(id))
                        .build()
        );
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<?>> reject(@PathVariable Long id){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet rejected")
                        .data(service.rejectTimesheet(id))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet fetched")
                        .data(service.getTimesheetById(id))
                        .build()
        );
    }

    @GetMapping("/person/{personId}/date/{date}")
    public ResponseEntity<ApiResponse<?>> getByPersonAndDate(
            @PathVariable Long personId,
            @PathVariable String date){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet fetched")
                        .data(service.getByPersonAndDate(personId, date))
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheets fetched")
                        .data(service.getAllTimesheets())
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id){

        service.deleteTimesheet(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet deleted")
                        .build()
        );
    }

}