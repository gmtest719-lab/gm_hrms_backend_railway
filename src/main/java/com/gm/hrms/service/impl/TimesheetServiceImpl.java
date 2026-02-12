package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TimesheetRequestDTO;
import com.gm.hrms.dto.response.TimesheetResponseDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.Project;
import com.gm.hrms.entity.Timesheet;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.TimesheetMapper;
import com.gm.hrms.mapper.TimesheetStatus;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.repository.ProjectRepository;
import com.gm.hrms.repository.TimesheetRepository;
import com.gm.hrms.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository repo;
    private final EmployeeRepository employeeRepo;
    private final ProjectRepository projectRepo;

    @Override
    public TimesheetResponseDTO create(Long employeeId, TimesheetRequestDTO dto) {

        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Project project = projectRepo.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Timesheet t = Timesheet.builder()
                .employee(emp)
                .project(project)
                .workDate(dto.getWorkDate())
                .hours(dto.getHours())
                .description(dto.getDescription())
                .status(TimesheetStatus.DRAFT)
                .build();

        return TimesheetMapper.toResponse(repo.save(t));
    }

    @Override
    public TimesheetResponseDTO submit(Long timesheetId) {

        Timesheet t = repo.findById(timesheetId)
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found"));

        t.setStatus(TimesheetStatus.SUBMITTED);

        return TimesheetMapper.toResponse(repo.save(t));
    }

    @Override
    public TimesheetResponseDTO approve(Long timesheetId) {

        Timesheet t = repo.findById(timesheetId)
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found"));

        t.setStatus(TimesheetStatus.APPROVED);

        return TimesheetMapper.toResponse(repo.save(t));
    }

    @Override
    public TimesheetResponseDTO reject(Long timesheetId) {

        Timesheet t = repo.findById(timesheetId)
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found"));

        t.setStatus(TimesheetStatus.REJECTED);

        return TimesheetMapper.toResponse(repo.save(t));
    }

    @Override
    public List<TimesheetResponseDTO> getByEmployee(Long employeeId) {

        return repo.findByEmployeeId(employeeId)
                .stream()
                .map(TimesheetMapper::toResponse)
                .toList();
    }

    @Override
    public List<TimesheetResponseDTO> getAll() {

        return repo.findAll()
                .stream()
                .map(TimesheetMapper::toResponse)
                .toList();
    }
}
