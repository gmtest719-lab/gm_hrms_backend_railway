package com.gm.hrms.repository;

import com.gm.hrms.entity.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonalInformationRepository extends JpaRepository<PersonalInformation, Long> {


    @Query("""
        SELECT p FROM PersonalInformation p
        WHERE p.active = true
        ORDER BY p.firstName, p.lastName
    """)
    List<PersonalInformation> findAllActive();

    @Query("""
        SELECT p FROM PersonalInformation p
        JOIN p.workProfile wp
        WHERE p.active = true
        AND wp.department.id = :departmentId
        ORDER BY p.firstName, p.lastName
    """)
    List<PersonalInformation> findActiveByDepartmentId(
            @Param("departmentId") Long departmentId);
}