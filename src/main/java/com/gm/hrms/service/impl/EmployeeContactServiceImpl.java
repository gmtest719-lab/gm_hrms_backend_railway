package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.EmployeeContactDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeContact;
import com.gm.hrms.repository.EmployeeContactRepository;
import com.gm.hrms.service.EmployeeContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeContactServiceImpl implements EmployeeContactService {

    private final EmployeeContactRepository contactRepository;

    @Override
    public void createContact(Employee employee, EmployeeContactDTO dto) {

        EmployeeContact contact = new EmployeeContact();

        contact.setEmployee(employee);
        contact.setPersonalEmail(dto.getPersonalEmail());
        contact.setOfficeEmail(dto.getOfficeEmail());
        contact.setPersonalPhone(dto.getPersonalPhone());
        contact.setEmergencyPhone(dto.getEmergencyPhone());

        contactRepository.save(contact);
    }
    @Override
    public void updateContact(Employee employee, EmployeeContactDTO dto) {

        EmployeeContact contact =
                contactRepository.findByEmployee(employee)
                        .orElse(new EmployeeContact());

        contact.setEmployee(employee);

        if(dto.getPersonalEmail() != null){
            contact.setPersonalEmail(dto.getPersonalEmail());
        }

        if(dto.getOfficeEmail() != null){
            contact.setOfficeEmail(dto.getOfficeEmail());
        }

        if(dto.getPersonalPhone() != null){
            contact.setPersonalPhone(dto.getPersonalPhone());
        }

        if(dto.getEmergencyPhone() != null){
            contact.setEmergencyPhone(dto.getEmergencyPhone());
        }

        contactRepository.save(contact);
    }



}
