package com.starter.performance.service.impl;

import com.starter.performance.config.MailComponent;
import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.Name;
import com.starter.performance.domain.PerformanceSchedule;
import com.starter.performance.domain.PerformanceStatus;
import com.starter.performance.domain.Reservation;
import com.starter.performance.domain.ReservationStatus;
import com.starter.performance.exception.impl.ExistReservationException;
import com.starter.performance.exception.impl.NotPresentTicketException;
import com.starter.performance.exception.impl.NotProperPerformanceStatusException;
import com.starter.performance.exception.impl.NotProperReservationDateException;
import com.starter.performance.exception.impl.NotProperTicketNumException;
import com.starter.performance.repository.MemberRepository;
import com.starter.performance.repository.PerformanceScheduleRepository;
import com.starter.performance.repository.ReservationRepository;
import com.starter.performance.service.ReservationService;
import com.starter.performance.service.dto.ReservationResponseDto;
import com.starter.performance.controller.dto.ResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final MailComponent mailComponent;

    private final static String RESERVATION_MESSAGE = "예매가 완료되었습니다.";
    private final static String SHOW_MESSAGE = "예매 목록을 불러옵니다.";

    private Long possibleReservationDate;
    private final static Long VIP_POSSIBLE_DATE = 7L;
    private final static Long STANDARD_POSSIBLE_DATE = 6L;

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

        /** 이미 예매한 정보가 있는지 확인 */
        existReservation(member, performanceSchedule);

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

        /** 예약 확인 이메일 보내기 */
        sendMail(email, savedReservation);

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(RESERVATION_MESSAGE)
            .body(new ReservationResponseDto(
                savedReservation.getPerformanceName(),
                savedReservation.getReservedTicketNum(),
                savedReservation.getPerformanceDate(),
                savedReservation.getReservationDate()
            ))
            .build();
    }

    // 예매 완료 후 확인 메일 보내기
    @Override
    public void sendMail(String email, Reservation savedReservation) {
        if (reservationRepository.findById(savedReservation.getId()).isEmpty()) {
            throw new RuntimeException("예매 내역이 없습니다.");
        }

        String subject = "[공연 예매 사이트] 예매가 무사히 완료되었습니다.";
        String text = email + "님의 [" + savedReservation.getPerformanceName()
            + "] 예매가 무사히 완료되었습니다. 자세한 내용은 예매목록보기에서 확인할 수 있습니다.";

        mailComponent.sendMail(email, subject, text);

    }

    // 예매 목록 보기
    @Override
    public ResponseDto showReservations(Authentication auth, Pageable pageable) {
        List<ReservationResponseDto> dtoList = new ArrayList<>();

        String email = auth.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(IllegalArgumentException::new);

        Page<Reservation> list = reservationRepository.findAllByMember(member, pageable);

        for (Reservation setReservation : list) {
            ReservationResponseDto dto = new ReservationResponseDto(
                setReservation.getPerformanceName(),
                setReservation.getReservedTicketNum(),
                setReservation.getPerformanceDate(),
                setReservation.getReservationDate());

            dtoList.add(dto);
        }

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(SHOW_MESSAGE)
            .body(dtoList)
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

    // 예매 가능한 날짜인지 등급별 확인 - VIP는 공연 7일전 예매 가능, STANDARD는 공연 6일전 예매 가능
    @Override
    public void checkReservationPossibleDate(PerformanceSchedule performanceSchedule, Name name) {
        if (name.equals(Name.VIP)) {
            possibleReservationDate = VIP_POSSIBLE_DATE;
        } else if (name.equals(Name.STANDARD)) {
            possibleReservationDate = STANDARD_POSSIBLE_DATE;
        }

        LocalDateTime performanceDate = performanceSchedule.getPerformanceDate()
            .minusDays(possibleReservationDate).withHour(0).withMinute(0);
        LocalDateTime reservationPossibleDate = LocalDateTime.now();

        log.info("예매 가능 날짜 : " + performanceDate);

        log.info("isBefore()로 찍히는 날짜 : "
            + reservationPossibleDate.isBefore(performanceDate));

        if (reservationPossibleDate.isBefore(performanceDate)) {
            throw new NotProperReservationDateException();
        }

    }

    // performanceState == TICKETING인지 확인. (예매 가능 상태인지 확인)
    @Override
    public void checkPerformanceState(PerformanceSchedule performanceSchedule) {
        if (!performanceSchedule.getPerformanceStatus().equals(PerformanceStatus.TICKETING)) {
            log.info("공연 상태 : " + performanceSchedule.getPerformanceStatus().toString());
            throw new NotProperPerformanceStatusException();
        }
    }

    // 만약 이미 예매한 정보가 reservation 테이블에 있는지 확인 - 있다면 예매 진행 불가
    public void existReservation(Member member, PerformanceSchedule performanceSchedule) {
        if (reservationRepository.existsByMemberAndPerformanceSchedule(member, performanceSchedule)) {
            throw new ExistReservationException();
        }
    }

}
