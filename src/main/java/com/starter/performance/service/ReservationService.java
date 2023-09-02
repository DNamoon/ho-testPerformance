package com.starter.performance.service;

import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.domain.Name;
import com.starter.performance.domain.PerformanceSchedule;
import com.starter.performance.domain.Reservation;
import com.starter.performance.controller.dto.ResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface ReservationService {

    ResponseDto makeReservation(Long performanceScheduleId, ReservationRequestDto dto, Authentication auth);

    ResponseDto showReservations(Authentication auth, Pageable pageable);

    // 예매 완료 후 확인 메일 보내기
    void sendMail(String email, Reservation reservation);

    @Transactional
    void updateTicket(Long performanceScheduleId, Integer ticket);

    void checkReservationTicketNum(Integer ticket);

    void checkReservationPossibleDate(PerformanceSchedule performanceSchedule, Name name);

    void checkPerformanceState(PerformanceSchedule performanceSchedule);

}
