package com.starter.performance.service.impl;

import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.Name;
import com.starter.performance.domain.PerformanceSchedule;
import com.starter.performance.domain.PerformanceStatus;
import com.starter.performance.domain.Reservation;
import com.starter.performance.domain.ReservationStatus;
import com.starter.performance.exception.impl.NotPresentTicketException;
import com.starter.performance.exception.impl.NotProperPerformanceStatusException;
import com.starter.performance.exception.impl.NotProperReservationDateException;
import com.starter.performance.exception.impl.NotProperTicketNumException;
import com.starter.performance.repository.MemberRepository;
import com.starter.performance.repository.PerformanceScheduleRepository;
import com.starter.performance.repository.ReservationRepository;
import com.starter.performance.service.ReservationService;
import com.starter.performance.service.dto.ReservationResponseDto;
import com.starter.performance.service.dto.ResponseDto;
import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final MemberRepository memberRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;
    private final ReservationRepository reservationRepository;

    private final EntityManager entityManager;

    private final static String MESSAGE = "예매가 완료되었습니다.";

    private Long isPossibleReservation;
    private final static Long VIP_POSSIBLE = 7L;
    private final static Long NORMAL_POSSIBLE = 6L;

    @Override
    public ResponseDto makeReservation(Long performanceScheduleId, ReservationRequestDto dto, Authentication auth) {

        String email = auth.getName();
        Integer ticket = Integer.parseInt(dto.getReservedTicketNum());

        /** 예매 요청 티켓수가 1 또는 2매인지 확인 */
        checkReservationTicketNum(ticket);

        Member member = memberRepository.findByEmail(email).orElseThrow(IllegalArgumentException::new);
        Name name = member.getRating().getName();

        PerformanceSchedule performanceSchedule = performanceScheduleRepository.findById(performanceScheduleId)
            .orElseThrow(IllegalArgumentException::new);
        /** 예매 가능한 상태인지 확인 - performance_state == TICKETING */
        checkPerformanceState(performanceSchedule);

        /** 예매 가능한 날짜인지 확인 */
        checkReservationPossibleDate(performanceSchedule, name);

        /** 티켓 남아있는지 확인 */
        updateTicket(performanceSchedule.getId(), ticket);

        Reservation reservation = Reservation.builder()
            .member(member)
            .performanceSchedule(performanceSchedule)
            .performanceName(performanceSchedule.getPerformance().getName())
            .performanceDate(performanceSchedule.getPerformanceDate())
            .reservedTicketNum(Integer.parseInt(dto.getReservedTicketNum()))
            .reservationStatus(ReservationStatus.YES)
            .reservationDate(LocalDateTime.now())
//                LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:"))))
            .build();

        log.info("ticketQuantity : " + dto.getReservedTicketNum());
        Reservation savedReservation = reservationRepository.save(reservation);

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(MESSAGE)
            .data(new ReservationResponseDto(
                savedReservation.getPerformanceName(),
                savedReservation.getReservedTicketNum(),
                savedReservation.getPerformanceDate(),
                savedReservation.getReservationDate()
            ))
            .build();
    }

    // JPA 더티 체킹 - performanceSchedule의 티켓 수량 변경. (예매한 표만큼)
    @Transactional
    @Override
    public void updateTicket(Long id, Integer ticket) {

        PerformanceSchedule performanceSchedule = entityManager
            .find(PerformanceSchedule.class, id);

        int leftTicket = performanceSchedule.getTicket_quantity() - ticket;
        log.info("db에 남아있는 티켓 수 : " + performanceSchedule.getTicket_quantity());

        log.info("회원이 예매한 티켓 수 : " + ticket);

        log.info("예매하고 남은 티켓 수 : " + leftTicket);

        if (leftTicket < 0) {
            throw new NotPresentTicketException();
        }

        performanceSchedule.setTicket_quantity(leftTicket);

    }

    // 예매 티켓 수가 1 또는 2매가 아니면 예외 처리
    @Override
    public void checkReservationTicketNum(Integer ticket) {
        if (ticket < 1 || ticket > 2) {
            throw new NotProperTicketNumException();
        }
    }

    @Override
    // 예매 가능한 상태인지 확인 - VIP는 공연 7일전 예매 가능, NORMAL은 공연 6일전 예매 가능
    public void checkReservationPossibleDate(PerformanceSchedule performanceSchedule, Name name) {
        if (name.equals(Name.VIP)) {
            isPossibleReservation = VIP_POSSIBLE;
        } else if (name.equals(Name.NORMAL)) {
            isPossibleReservation = NORMAL_POSSIBLE;
        }

        LocalDateTime performanceDate = performanceSchedule.getPerformanceDate()
            .minusDays(isPossibleReservation).withHour(0).withMinute(0);
        LocalDateTime reservationPossibleDate = LocalDateTime.now();

        log.info("예매 가능 날짜 : " + performanceDate);

        log.info("isBefore()로 찍히는 날짜 : "
            + reservationPossibleDate.isBefore(performanceDate));

        if (reservationPossibleDate.isBefore(performanceDate)) {
            throw new NotProperReservationDateException();
        }

    }

    @Override
    public void checkPerformanceState(PerformanceSchedule performanceSchedule) {
        if (!performanceSchedule.getPerformanceStatus().equals(PerformanceStatus.TICKETING)) {
            log.info("공연 상태 : " + performanceSchedule.getPerformanceStatus().toString());
            throw new NotProperPerformanceStatusException();
        }
    }

}
