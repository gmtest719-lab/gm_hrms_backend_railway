package com.gm.hrms.repository;

import com.gm.hrms.entity.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalInformationRepository extends JpaRepository<PersonalInformation, Long> {
}