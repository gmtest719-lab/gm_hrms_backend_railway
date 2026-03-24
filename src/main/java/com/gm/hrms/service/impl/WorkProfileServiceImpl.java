package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.WorkProfileRequestDTO;
import com.gm.hrms.dto.response.WorkProfileResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.WorkProfileMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.WorkProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkProfileServiceImpl implements WorkProfileService {

    private final WorkProfileRepository repository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final BranchRepository branchRepository;
    private final ShiftRepository shiftRepository;
    private final PersonalInformationRepository personalInformationRepository;

    // =====================================================
    // ================= CREATE =============================
    // =====================================================

    @Override
    public WorkProfileResponseDTO create(Long personalInformationId, WorkProfileRequestDTO dto) {

        PersonalInformation person = personalInformationRepository.findById(personalInformationId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal information not found"));

        Department dept = null;
        Designation desig = null;
        Branch branch = null;
        Shift shift = null;
        WorkProfile reportingManager = null;

        if (dto.getDepartmentId() != null) {
            dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        }

        if (dto.getDesignationId() != null) {
            desig = designationRepository.findById(dto.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));
        }

        if (dto.getBranchId() != null) {
            branch = branchRepository.findById(dto.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        }

        if (dto.getShiftId() != null) {
            shift = shiftRepository.findById(dto.getShiftId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
        }

        if (dto.getReportingManagerProfileId() != null) {
            reportingManager = repository.findById(dto.getReportingManagerProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reporting manager not found"));
        }

        WorkProfile entity = WorkProfileMapper.toEntity(
                dto,
                dept,
                desig,
                branch,
                shift,
                reportingManager
        );

        entity.setPersonalInformation(person);

        repository.save(entity);

        return WorkProfileMapper.toResponse(entity);
    }

    // =====================================================
    // ================= UPDATE (PATCH) ====================
    // =====================================================

    @Override
    public WorkProfileResponseDTO update(Long id, WorkProfileRequestDTO dto) {

        WorkProfile entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkProfile not found"));

        Department dept = null;
        Designation desig = null;
        Branch branch = null;
        Shift shift = null;
        WorkProfile reportingManager = null;

        if (dto.getDepartmentId() != null) {
            dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        }

        if (dto.getDesignationId() != null) {
            desig = designationRepository.findById(dto.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));
        }

        if (dto.getBranchId() != null) {
            branch = branchRepository.findById(dto.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        }

        if (dto.getShiftId() != null) {
            shift = shiftRepository.findById(dto.getShiftId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
        }

        if (dto.getReportingManagerProfileId() != null) {
            reportingManager = repository.findById(dto.getReportingManagerProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reporting manager not found"));
        }

        WorkProfileMapper.patchEntity(
                entity,
                dto,
                dept,
                desig,
                branch,
                shift,
                reportingManager
        );

        repository.save(entity);

        return WorkProfileMapper.toResponse(entity);
    }

    // =====================================================
    // ================= GET BY ID =========================
    // =====================================================

    @Override
    public WorkProfileResponseDTO getById(Long id) {

        WorkProfile entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkProfile not found"));

        return WorkProfileMapper.toResponse(entity);
    }

    // =====================================================
    // ================= GET ALL ===========================
    // =====================================================

    @Override
    public List<WorkProfileResponseDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(WorkProfileMapper::toResponse)
                .toList();
    }

    // =====================================================
    // ================= DELETE ============================
    // =====================================================

    @Override
    public void delete(Long id) {

        WorkProfile entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkProfile not found"));

        repository.delete(entity);
    }
}