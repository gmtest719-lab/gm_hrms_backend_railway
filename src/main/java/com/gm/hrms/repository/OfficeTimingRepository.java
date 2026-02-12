package com.gm.hrms.repository;

import com.gm.hrms.entity.OfficeTiming;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfficeTimingRepository extends JpaRepository<OfficeTiming, Long> {

    Optional<OfficeTiming> findFirstByOrderByIdAsc();
}
