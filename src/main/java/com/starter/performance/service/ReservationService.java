package com.starter.performance.service;

import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.controller.dto.ResponseDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.Name;
import com.starter.performance.domain.PerformanceSchedule;
import com.starter.performance.domain.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface ReservationService {

    @Transactional
    ResponseDto makeReservation(Long performanceId, Long performanceScheduleId, ReservationRequestDto dto,
        Authentication auth);

    ResponseDto showReservations(Authentication auth, Pageable pageable);

    ResponseDto cancelReservation(Authentication auth, Long reservationId);

//    ResponseDto changeReservation(Authentication auth, ChangeReservationDto dto);

    // 예매 완료 후 확인 메일 보내기
    void sendMail(String email, Reservation reservation);

    @Transactional
    void updateTicketForStandard(Long performanceScheduleId, Integer ticket);

    @Transactional
    void updateTicket(Long id, Integer ticket, Name name);

    void checkPerformance(PerformanceSchedule performanceSchedule, Long performanceId);

    void checkReservationTicketNum(Integer ticket);

    void checkReservationPossibleDate(PerformanceSchedule performanceSchedule, Name name);

    void checkPerformanceState(PerformanceSchedule performanceSchedule);

    // 예매한 회원이 맞는지 확인
    void checkReservationAndMember(Reservation reservation, Member member);

    void checkCanceledReservation(Reservation reservation);
}
