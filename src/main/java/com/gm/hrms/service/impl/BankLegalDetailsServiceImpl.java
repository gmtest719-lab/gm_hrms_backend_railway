package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.BankLegalDetailsRequestDTO;
import com.gm.hrms.dto.response.BankLegalDetailsResponseDTO;
import com.gm.hrms.entity.BankLegalDetails;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.BankLegalDetailsMapper;
import com.gm.hrms.repository.BankLegalDetailsRepository;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.service.BankLegalDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankLegalDetailsServiceImpl implements BankLegalDetailsService {

    private final BankLegalDetailsRepository repository;
    private final EmployeeRepository employeeRepository;

    @Override
    public BankLegalDetailsResponseDTO saveOrUpdate(
            Long employeeId,
            BankLegalDetailsRequestDTO dto) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        BankLegalDetails details = repository.findByEmployeeId(employeeId)
                .orElse(null);

        if (details == null) {
            details = BankLegalDetailsMapper.toEntity(dto, employee);
        } else {
            BankLegalDetailsMapper.updateEntity(details, dto);
        }

        repository.save(details);

        return BankLegalDetailsMapper.toResponse(details);
    }

    @Override
    public BankLegalDetailsResponseDTO getMyDetails(Long employeeId) {

        BankLegalDetails details = repository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank details not found"));

        return BankLegalDetailsMapper.toResponse(details);
    }
}