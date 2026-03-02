package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.PersonalInformationRequestDTO;
import com.gm.hrms.dto.response.PersonalInformationResponseDTO;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.entity.PersonalInformationContact;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.PersonalInformationMapper;
import com.gm.hrms.repository.PersonContactRepository;
import com.gm.hrms.repository.PersonalInformationRepository;
import com.gm.hrms.service.EmployeeAddressService;
import com.gm.hrms.service.EmployeeBankDetailsService;
import com.gm.hrms.service.PersonalInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalInformationServiceImpl implements PersonalInformationService {

    private final PersonalInformationRepository personalInformationRepository;
    private final PersonContactRepository contactRepository;
    private final EmployeeAddressService employeeAddressService;
    private final EmployeeBankDetailsService employeeBankDetailsService;



    // ================= CREATE =================

    @Override
    public PersonalInformationResponseDTO create(PersonalInformationRequestDTO dto) {

        // 1️⃣ Validate personal email
        if (contactRepository.existsByPersonalEmail(dto.getPersonalEmail())) {
            throw new DuplicateResourceException("Personal email already exists");
        }

        // 2️⃣ Validate office email (only if provided)
        if (dto.getOfficeEmail() != null &&
                contactRepository.existsByOfficeEmail(dto.getOfficeEmail())) {
            throw new DuplicateResourceException("Office email already exists");
        }

        // 3️⃣ Save PersonalInformation
        PersonalInformation personalInformation =
                PersonalInformationMapper.toEntity(dto);

        personalInformation = personalInformationRepository.save(personalInformation);

        if (dto.getBankDetails() != null) {
            employeeBankDetailsService.saveOrUpdate(personalInformation, dto.getBankDetails());
        }

        if (dto.getAddress() != null) {
            employeeAddressService.saveOrUpdate(personalInformation, dto.getAddress());
        }

        // 4️⃣ Create Contact
        PersonalInformationContact contact =
                PersonalInformationContact.builder()
                        .personalInformation(personalInformation)
                        .personalPhone(dto.getPersonalPhone())
                        .emergencyPhone(dto.getEmergencyPhone())
                        .personalEmail(dto.getPersonalEmail())
                        .officeEmail(dto.getOfficeEmail()) // use correct field name
                        .build();

        contact = contactRepository.save(contact);

        // 🔥 IMPORTANT: Set inverse side to avoid NPE
        personalInformation.setContact(contact);

        return PersonalInformationMapper.toResponse(personalInformation);
    }// ================= UPDATE =================

    @Override
    @Transactional
    public PersonalInformationResponseDTO update(Long id,
                                                 PersonalInformationRequestDTO dto) {

        PersonalInformation personalInformation = personalInformationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Personal information not found"));

        //  PATCH MAIN ENTITY USING MAPPER
        PersonalInformationMapper.updateEntity(personalInformation, dto);

        // ===== CONTACT PATCH =====
        PersonalInformationContact contact = personalInformation.getContact();

        if (contact == null) {
            contact = new PersonalInformationContact();
            contact.setPersonalInformation(personalInformation);
        }

        //  Duplicate check ONLY if email is provided and changed
        if (dto.getPersonalEmail() != null &&
                (contact.getPersonalEmail() == null ||
                        !contact.getPersonalEmail().equals(dto.getPersonalEmail()))) {

            if (contactRepository.existsByPersonalEmail(dto.getPersonalEmail())) {
                throw new DuplicateResourceException("Personal email already exists");
            }
        }
        if (dto.getOfficeEmail() != null &&
                (contact.getOfficeEmail() == null ||
                        !contact.getOfficeEmail().equals(dto.getOfficeEmail()))) {

            if (contactRepository.existsByPersonalEmail(dto.getOfficeEmail())) {
                throw new DuplicateResourceException("Office email already exists");
            }
        }

        if (dto.getBankDetails() != null) {
            employeeBankDetailsService.saveOrUpdate(personalInformation, dto.getBankDetails());
        }

        if (dto.getAddress() != null) {
            employeeAddressService.saveOrUpdate(personalInformation, dto.getAddress());
        }

        //  PATCH CONTACT USING MAPPER
        PersonalInformationMapper.updateContact(contact, dto);

        contactRepository.save(contact);

        return PersonalInformationMapper.toResponse(personalInformation);
    }
    // ================= GET BY ID =================

    @Override
    @Transactional(readOnly = true)
    public PersonalInformationResponseDTO getById(Long id) {

        PersonalInformation personalInformation = personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonalInformation not found"));

        return PersonalInformationMapper.toResponse(personalInformation);
    }

    // ================= GET ALL =================

    @Override
    @Transactional(readOnly = true)
    public List<PersonalInformationResponseDTO> getAll() {

        return personalInformationRepository.findAll()
                .stream()
                .map(PersonalInformationMapper::toResponse)
                .toList();
    }

    // ================= DELETE (SOFT DELETE) =================

    @Override
    public void delete(Long id) {

        PersonalInformation personalInformation = personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonalInformation not found"));

        personalInformation.setActive(false);
    }
}