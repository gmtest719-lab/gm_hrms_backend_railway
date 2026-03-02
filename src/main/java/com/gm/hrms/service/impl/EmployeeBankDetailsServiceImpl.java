package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.EmployeeBankDetailsRequestDTO;
import com.gm.hrms.entity.BankLegalDetails;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.repository.BankLegalDetailsRepository;
import com.gm.hrms.service.EmployeeBankDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeBankDetailsServiceImpl
        implements EmployeeBankDetailsService {

    private final BankLegalDetailsRepository repository;

    @Override
    public void saveOrUpdate(PersonalInformation personalInformation,
                             EmployeeBankDetailsRequestDTO dto) {

        BankLegalDetails bank =
                repository.findByPersonalInformation(personalInformation)
                        .orElse(new BankLegalDetails());

        bank.setPersonalInformation(personalInformation);

        if (dto.getBankName() != null)
            bank.setBankName(dto.getBankName());

        if (dto.getAccountNumber() != null)
            bank.setAccountNumber(dto.getAccountNumber());

        if (dto.getIfscCode() != null)
            bank.setIfscCode(dto.getIfscCode());

        if (dto.getPanNumber() != null)
            bank.setPanNumber(dto.getPanNumber());

        if (dto.getAadhaarNumber() != null)
            bank.setAadhaarNumber(dto.getAadhaarNumber());

        if (dto.getUanNumber() != null)
            bank.setUanNumber(dto.getUanNumber());

        if (dto.getEsicNumber() != null)
            bank.setEsicNumber(dto.getEsicNumber());

        repository.save(bank);
    }
}