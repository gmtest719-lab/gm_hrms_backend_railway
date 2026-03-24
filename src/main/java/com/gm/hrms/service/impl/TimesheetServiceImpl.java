package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TimesheetEntryDTO;
import com.gm.hrms.dto.request.TimesheetRequestDTO;
import com.gm.hrms.dto.response.TimesheetResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.TimesheetStatus;
import com.gm.hrms.mapper.TimesheetMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.TimesheetService;
import com.gm.hrms.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final PersonalInformationRepository personRepository;
    private final ProjectRepository projectRepository;

    @Override
    public TimesheetResponseDTO createOrUpdateTimesheet(TimesheetRequestDTO request){

        PersonalInformation person =
                personRepository.findById(request.getPersonId())
                        .orElseThrow(() -> new RuntimeException("Person not found"));

        Timesheet timesheet =
                timesheetRepository.findByPerson_IdAndWorkDate(
                        request.getPersonId(),
                        request.getWorkDate()
                ).orElse(
                        Timesheet.builder()
                                .person(person)
                                .workDate(request.getWorkDate())
                                .entries(new ArrayList<>())
                                .status(TimesheetStatus.DRAFT)
                                .build()
                );

        timesheet.getEntries().clear();

        int totalMinutes = 0;

        for(TimesheetEntryDTO entryDTO : request.getEntries()){

            Project project =
                    projectRepository.findById(entryDTO.getProjectId())
                            .orElseThrow(() -> new RuntimeException("Project not found"));

            int minutes = TimeUtil.toMinutes(entryDTO.getWorkedTime());

            totalMinutes += minutes;

            TimesheetEntry entry =
                    TimesheetEntry.builder()
                            .timesheet(timesheet)
                            .project(project)
                            .workedMinutes(minutes)
                            .taskName(entryDTO.getTaskName())
                            .description(entryDTO.getDescription())
                            .build();

            timesheet.getEntries().add(entry);
        }

        // validation
        if(totalMinutes > 480){
            throw new RuntimeException("Daily work hours cannot exceed 8 hours");
        }

        timesheet.setTotalMinutes(totalMinutes);

        Timesheet saved = timesheetRepository.save(timesheet);

        return TimesheetMapper.toResponse(saved);
    }

    @Override
    public TimesheetResponseDTO submitTimesheet(Long timesheetId){

        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        timesheet.setStatus(TimesheetStatus.SUBMITTED);
        timesheet.setSubmittedAt(LocalDateTime.now());

        return TimesheetMapper.toResponse(timesheetRepository.save(timesheet));
    }

    @Override
    public TimesheetResponseDTO approveTimesheet(Long timesheetId){

        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        timesheet.setStatus(TimesheetStatus.APPROVED);
        timesheet.setApprovedAt(LocalDateTime.now());

        return TimesheetMapper.toResponse(timesheetRepository.save(timesheet));
    }

    @Override
    public TimesheetResponseDTO rejectTimesheet(Long timesheetId){

        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        timesheet.setStatus(TimesheetStatus.REJECTED);

        return TimesheetMapper.toResponse(timesheetRepository.save(timesheet));
    }

    @Override
    public TimesheetResponseDTO getTimesheetById(Long id){

        Timesheet timesheet = timesheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        return TimesheetMapper.toResponse(timesheet);
    }

    @Override
    public TimesheetResponseDTO getByPersonAndDate(Long personId, String date){

        Timesheet timesheet = timesheetRepository
                .findByPerson_IdAndWorkDate(
                        personId,
                        LocalDate.parse(date)
                )
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        return TimesheetMapper.toResponse(timesheet);
    }

    @Override
    public List<TimesheetResponseDTO> getAllTimesheets(){

        return timesheetRepository.findAll()
                .stream()
                .map(TimesheetMapper::toResponse)
                .toList();
    }
    @Override
    public void deleteTimesheet(Long id){

        Timesheet timesheet = timesheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        timesheetRepository.delete(timesheet);
    }

}