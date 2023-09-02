package com.starter.performance.repository;

import com.starter.performance.domain.PerformanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Long> {

}
