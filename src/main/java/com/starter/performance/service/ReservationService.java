package com.starter.performance.service;

import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.domain.Name;
import com.starter.performance.domain.PerformanceSchedule;
import com.starter.performance.service.dto.ResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface ReservationService {

    ResponseDto makeReservation(Long performanceScheduleId, ReservationRequestDto dto, Authentication auth);

    ResponseDto showReservations(Authentication auth, Pageable pageable);

    @Transactional
    void updateTicket(Long performanceScheduleId, Integer ticket);

    void checkReservationTicketNum(Integer ticket);

    void checkReservationPossibleDate(PerformanceSchedule performanceSchedule, Name name);

    void checkPerformanceState(PerformanceSchedule performanceSchedule);

}
