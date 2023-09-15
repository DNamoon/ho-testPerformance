package com.starter.performance.service;

import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.controller.dto.ResponseDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.PerformanceSchedule;
import com.starter.performance.domain.RatingName;
import com.starter.performance.domain.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ReservationService {

    Reservation makeReservation(Long performanceId, Long performanceScheduleId, ReservationRequestDto dto,
        Authentication auth);

    ResponseDto showReservations(Authentication auth, Pageable pageable);

    ResponseDto cancelReservation(Authentication auth, Long reservationId);

    ResponseDto changeReservation(Authentication auth, ReservationRequestDto dto, Long reservationId);

    // 예매 완료 후 확인 메일 보내기
    void sendMail(String email, Reservation reservation);

    void updateTicketForStandard(PerformanceSchedule performanceSchedule, Integer ticket);

    void updateTicket(Long id, Integer ticket, RatingName ratingName);

    void checkPerformance(PerformanceSchedule performanceSchedule, Long performanceId);

    void checkReservationTicketNum(Integer ticket);

    void checkReservationPossibleDate(PerformanceSchedule performanceSchedule, RatingName ratingName);

    void checkPerformanceState(PerformanceSchedule performanceSchedule);

    void checkReservationMemberMatch(Reservation reservation, Member member);

    void checkCanceledReservation(Reservation reservation);
}
