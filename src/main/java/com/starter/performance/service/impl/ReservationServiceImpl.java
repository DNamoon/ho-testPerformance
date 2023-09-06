package com.starter.performance.service.impl;

import com.starter.performance.config.MailComponent;
import com.starter.performance.controller.dto.ChangeReservationDto;
import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.controller.dto.ResponseDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.Name;
import com.starter.performance.domain.PerformanceSchedule;
import com.starter.performance.domain.PerformanceStatus;
import com.starter.performance.domain.Reservation;
import com.starter.performance.domain.ReservationStatus;
import com.starter.performance.exception.impl.CanNotVipReservationException;
import com.starter.performance.exception.impl.CanceledReservationException;
import com.starter.performance.exception.impl.ExistReservationException;
import com.starter.performance.exception.impl.NotPresentTicketException;
import com.starter.performance.exception.impl.NotProperPerformanceStatusException;
import com.starter.performance.exception.impl.NotProperReservationDateException;
import com.starter.performance.exception.impl.NotProperTicketNumException;
import com.starter.performance.exception.impl.NotValidMember;
import com.starter.performance.exception.impl.NotValidPerformanceException;
import com.starter.performance.repository.MemberRepository;
import com.starter.performance.repository.PerformanceScheduleRepository;
import com.starter.performance.repository.ReservationRepository;
import com.starter.performance.service.ReservationService;
import com.starter.performance.service.dto.ReservationResponseDto;
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
    private final static String CANCEL_MESSAGE = "예매가 취소되었습니다.";
    private final static String CHANGE_MESSAGE = "예매 정보를 수정했습니다. 예매 티켓 수가 변동됩니다.";

    private Long possibleReservationDate;
    private final static Long VIP_POSSIBLE_DATE = 7L;
    private final static Long STANDARD_POSSIBLE_DATE = 6L;

    @Override
    public ResponseDto makeReservation(Long performanceId, Long performanceScheduleId, ReservationRequestDto dto,
        Authentication auth) {

        String email = auth.getName();
        Integer ticket = Integer.parseInt(dto.getReservedTicketNum());

        /** 예매 요청 티켓수가 1 또는 2매인지 확인 */
        checkReservationTicketNum(ticket);

        Member member = memberRepository.findByEmail(email).orElseThrow(IllegalArgumentException::new);
        Name name = member.getRating().getName();

        PerformanceSchedule performanceSchedule = performanceScheduleRepository.findById(performanceScheduleId)
            .orElseThrow(IllegalArgumentException::new);

        /** 공연 정보가 올바른지 확인 */
        checkPerformance(performanceSchedule, performanceId);

        /** 이미 예매한 정보가 있는지 확인 */
        existReservation(member, performanceSchedule);

        /** 예매 가능한 상태인지 확인 - performance_state == TICKETING */
        checkPerformanceState(performanceSchedule);

        /** 예매 가능한 날짜인지 확인 */
        checkReservationPossibleDate(performanceSchedule, name);

        /** 티켓 남아있는지 확인 후 티켓 수량 변경 */
        updateTicket(performanceSchedule.getId(), ticket, name);

        Reservation reservation = Reservation.builder()
            .member(member)
            .performanceSchedule(performanceSchedule)
            .performanceName(performanceSchedule.getPerformance().getName())
            .performanceDate(performanceSchedule.getPerformanceDate())
            .reservedTicketNum(Integer.parseInt(dto.getReservedTicketNum()))
            .reservationStatus(ReservationStatus.YES)
            .reservationDate(LocalDateTime.now())
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
                savedReservation.getReservationStatus(),
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
        StringBuilder text = new StringBuilder();
        text.append(email).append("님의 [").append(savedReservation.getPerformanceName())
            .append("] 예매가 무사히 완료되었습니다. 자세한 내용은 예매목록보기에서 확인할 수 있습니다.");

        mailComponent.sendMail(email, subject, text);

    }

    // 예매 목록 보기
    @Override
    public ResponseDto showReservations(Authentication auth, Pageable pageable) {
        List<ReservationResponseDto> dtoList = new ArrayList<>();

        String email = auth.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(IllegalArgumentException::new);

        Page<Reservation> list = reservationRepository.findAllByMember(member, pageable);

        for (Reservation savedReservation : list) {
            ReservationResponseDto dto = new ReservationResponseDto(
                savedReservation.getPerformanceName(),
                savedReservation.getReservedTicketNum(),
                savedReservation.getReservationStatus(),
                savedReservation.getPerformanceDate(),
                savedReservation.getReservationDate());

            dtoList.add(dto);
        }

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(SHOW_MESSAGE)
            .body(dtoList)
            .build();
    }

    // 예매 취소하기
    @Transactional
    @Override
    public ResponseDto cancelReservation(Authentication auth, Long reservationId) {

        Reservation reservation = entityManager.find(Reservation.class, reservationId);
        String email = auth.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(IllegalAccessError::new);

        /** 예매 정보의 회원정보와 로그인 회원 일치 여부 확인 */
        checkReservationAndMember(reservation, member);

        /** 예매가 되어있는지 확인*/
        checkCanceledReservation(reservation);

        reservation.setReservationStatus(ReservationStatus.NO);

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(CANCEL_MESSAGE)
            .body(new ReservationResponseDto(
                reservation.getPerformanceName(),
                reservation.getReservedTicketNum(),
                reservation.getReservationStatus(),
                reservation.getPerformanceDate(),
                reservation.getReservationDate()
            ))
            .build();
    }

    // 예매 수정하기
    @Transactional
    @Override
    public ResponseDto changeReservation(Authentication auth, ChangeReservationDto dto) {
        String email = auth.getName();
        int newTicketNum = Integer.parseInt(dto.getReservedTicketNum());
        Member member = memberRepository.findByEmail(email).orElseThrow(IllegalAccessError::new);

        Reservation reservation = entityManager.find(Reservation.class, dto.getReservationId());

        /** 예매 정보의 회원정보와 로그인 회원 일치 여부 확인 */
        checkReservationAndMember(reservation, member);

        /** 예매가 되어있는지 확인*/
        checkCanceledReservation(reservation);

        if (!reservation.getReservedTicketNum().equals(newTicketNum)) {
            PerformanceSchedule performanceSchedule = entityManager.find(PerformanceSchedule.class,
                reservation.getPerformanceSchedule().getId());

            performanceSchedule.setTicketQuantity(
                performanceSchedule.getTicketQuantity() + reservation.getReservedTicketNum());

            updateTicket(reservation.getPerformanceSchedule().getId(), Integer.parseInt(dto.getReservedTicketNum()),
                member.getRating().getName());

            reservation.setReservedTicketNum(Integer.parseInt(dto.getReservedTicketNum()));
        }

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(CHANGE_MESSAGE)
            .body(new ReservationResponseDto(
                reservation.getPerformanceName(),
                reservation.getReservedTicketNum(),
                reservation.getReservationStatus(),
                reservation.getPerformanceDate(),
                reservation.getReservationDate()
            ))
            .build();
    }

    // JPA 더티 체킹 - performanceSchedule의 티켓 수량 변경. (예매한 표만큼)

    /**
     * 일반 예매
     */
    @Transactional
    @Override
    public void updateTicketForStandard(Long performanceScheduleId, Integer ticket) {

        PerformanceSchedule performanceSchedule = entityManager
            .find(PerformanceSchedule.class, performanceScheduleId);

        int leftTicket = performanceSchedule.getTicketQuantity() - ticket;
        log.info("db에 남아있는 티켓 수 : " + performanceSchedule.getTicketQuantity());

        log.info("회원이 예매한 티켓 수 : " + ticket);

        log.info("예매하고 남은 티켓 수 : " + leftTicket);

        if (leftTicket < 0) {
            throw new NotPresentTicketException();
        }

        performanceSchedule.setTicketQuantity(leftTicket);

    }

    @Transactional
    @Override
    public void updateTicket(Long performanceScheduleId, Integer ticket, Name name) {

        PerformanceSchedule performanceSchedule = entityManager
            .find(PerformanceSchedule.class, performanceScheduleId);

        int leftTicket = performanceSchedule.getTicketQuantity() - ticket;
        int ticketForVip = performanceSchedule.getInitialTicketQuantity() * 8 / 10;

        log.info("ticketForVip 티켓: " + ticketForVip);
        log.info("leftTicket 티켓: " + leftTicket);

        if (name.equals(Name.VIP)) {
            if (ticketForVip > leftTicket) {
                log.info("vip티켓은 구매 못함");
            } else {
                updateTicketForStandard(performanceScheduleId, ticket);
                log.info("vip 티켓 예매");
                return;
            }

            if (performanceSchedule.getPerformanceDate().minusDays(STANDARD_POSSIBLE_DATE)
                .withHour(0).withMinute(0).isBefore(LocalDateTime.now())) {

                log.info("vip회원 일반 티켓 예매");
                updateTicketForStandard(performanceScheduleId, ticket);

            } else {
                throw new CanNotVipReservationException();
            }
        } else if (name.equals(Name.STANDARD)) {
            updateTicketForStandard(performanceScheduleId, ticket);
        }

    }

    @Override
    public void checkPerformance(PerformanceSchedule performanceSchedule, Long performanceId) {
        if (!performanceId.equals(performanceSchedule.getPerformance().getId())) {
            throw new NotValidPerformanceException();
        }
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

        LocalDateTime possibleDate = performanceSchedule.getPerformanceDate()
            .minusDays(possibleReservationDate).withHour(0).withMinute(0);
        LocalDateTime reservationTime = LocalDateTime.now();

        log.info("예매 가능 날짜 : " + possibleDate);

        if (reservationTime.isBefore(possibleDate)) {
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

        if (reservationRepository.existsByMemberAndPerformanceScheduleAndReservationStatus(
            member, performanceSchedule, ReservationStatus.YES
        )) {
            throw new ExistReservationException();
        }
    }

    // 예매한 회원이 맞는지 확인
    @Override
    public void checkReservationAndMember(Reservation reservation, Member member) {
        if (!member.equals(reservation.getMember())) {
            throw new NotValidMember();
        }
    }

    // 취소된 예매인지 확인
    @Override
    public void checkCanceledReservation(Reservation reservation) {
        if ((ReservationStatus.NO).equals(reservation.getReservationStatus())) {
            throw new CanceledReservationException();
        }
    }

}
