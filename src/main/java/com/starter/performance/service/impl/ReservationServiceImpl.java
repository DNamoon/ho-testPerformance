package com.starter.performance.service.impl;

import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.PerformanceSchedule;
import com.starter.performance.domain.Reservation;
import com.starter.performance.domain.ReservationStatus;
import com.starter.performance.repository.MemberRepository;
import com.starter.performance.repository.PerformanceScheduleRepository;
import com.starter.performance.repository.ReservationRepository;
import com.starter.performance.service.ReservationService;
import com.starter.performance.service.dto.ReservationResponseDto;
import com.starter.performance.service.dto.ResponseDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final MemberRepository memberRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;
    private final ReservationRepository reservationRepository;

    private final static String MESSAGE = "예매가 완료되었습니다.";

    @Override
    public ResponseDto makeReservation(Long performanceScheduleId, ReservationRequestDto dto, Authentication auth) {
        String email = auth.getName();

        Member member = memberRepository.findByEmail(email).orElseThrow(IllegalArgumentException::new);
//        Name name = member.getRating().getName();

        PerformanceSchedule performanceSchedule = performanceScheduleRepository.findById(performanceScheduleId)
            .orElseThrow(IllegalArgumentException::new);

        Reservation reservation = Reservation.builder()
            .member(member)
            .performanceSchedule(performanceSchedule)
            .performanceName(performanceSchedule.getPerformance().getName())
            .performanceDate(performanceSchedule.getPerformanceDate())
            .ticketQuantity(dto.getTicketQuantity())
            .reservationStatus(ReservationStatus.YES)
            .reservationDate(LocalDateTime.now())
            .build();

        log.info("ticketQuantity : " + dto.getTicketQuantity());
        Reservation savedReservation = reservationRepository.save(reservation);

        ReservationResponseDto responseDto = new ReservationResponseDto(
            savedReservation.getPerformanceName(),
            savedReservation.getPerformanceDate(),
            savedReservation.getReservationDate()
        );

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(MESSAGE)
            .data(responseDto)
            .build();
    }

}
