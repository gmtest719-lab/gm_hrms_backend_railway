package com.gm.hrms.repository;

import com.gm.hrms.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    boolean existsByHolidayNameAndHolidayDate(String holidayName, LocalDate holidayDate);

    boolean existsByHolidayDate(LocalDate holidayDate);
}