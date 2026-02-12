package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveApplyRequestDTO;
import com.gm.hrms.dto.response.LeaveResponseDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.LeaveApplication;
import com.gm.hrms.entity.LeaveType;
import com.gm.hrms.enums.LeaveStatus;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.LeaveMapper;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.repository.LeaveApplicationRepository;
import com.gm.hrms.repository.LeaveTypeRepository;
import com.gm.hrms.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveApplicationRepository leaveRepo;
    private final EmployeeRepository employeeRepo;
    private final LeaveTypeRepository leaveTypeRepo;

    @Override
    public LeaveResponseDTO applyLeave(Long employeeId, LeaveApplyRequestDTO dto) {

        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        LeaveType leaveType = leaveTypeRepo.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        LeaveApplication leave = LeaveApplication.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason())
                .status(LeaveStatus.PENDING)
                .employee(emp)
                .leaveType(leaveType)
                .build();

        return LeaveMapper.toResponse(leaveRepo.save(leave));
    }

    @Override
    public LeaveResponseDTO approve(Long leaveId) {

        LeaveApplication leave = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        leave.setStatus(LeaveStatus.APPROVED);

        return LeaveMapper.toResponse(leaveRepo.save(leave));
    }

    @Override
    public LeaveResponseDTO reject(Long leaveId) {

        LeaveApplication leave = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        leave.setStatus(LeaveStatus.REJECTED);

        return LeaveMapper.toResponse(leaveRepo.save(leave));
    }

    @Override
    public LeaveResponseDTO cancel(Long leaveId) {

        LeaveApplication leave = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        leave.setStatus(LeaveStatus.CANCELLED);
        leave.setCancelled(true);

        return LeaveMapper.toResponse(leaveRepo.save(leave));
    }

    @Override
    public List<LeaveResponseDTO> getByEmployee(Long employeeId) {

        return leaveRepo.findByEmployeeId(employeeId)
                .stream()
                .map(LeaveMapper::toResponse)
                .toList();
    }

    @Override
    public List<LeaveResponseDTO> getAll() {

        return leaveRepo.findAll()
                .stream()
                .map(LeaveMapper::toResponse)
                .toList();
    }
}
