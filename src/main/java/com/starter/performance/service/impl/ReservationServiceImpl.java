package com.starter.performance.service.impl;

import com.starter.performance.config.MailComponent;
import com.starter.performance.controller.dto.ChangeReservationDto;
import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.controller.dto.ResponseDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.PerformanceSchedule;
import com.starter.performance.domain.PerformanceStatus;
import com.starter.performance.domain.RatingName;
import com.starter.performance.domain.Reservation;
import com.starter.performance.domain.ReservationStatus;
import com.starter.performance.exception.impl.CanNotVipReservationException;
import com.starter.performance.exception.impl.CanceledReservationException;
import com.starter.performance.exception.impl.ExistReservationException;
import com.starter.performance.exception.impl.NotExistSuitableDataException;
import com.starter.performance.exception.impl.NotPresentTicketException;
import com.starter.performance.exception.impl.NotProperPerformanceStatusException;
import com.starter.performance.exception.impl.NotProperReservationDateException;
import com.starter.performance.exception.impl.NotProperTicketNumException;
import com.starter.performance.exception.impl.NotValidPerformanceException;
import com.starter.performance.exception.impl.NotValidReservationMemberException;
import com.starter.performance.repository.MemberRepository;
import com.starter.performance.repository.PerformanceScheduleRepository;
import com.starter.performance.repository.ReservationRepository;
import com.starter.performance.service.ReservationService;
import com.starter.performance.service.dto.ReservationResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private final MailComponent mailComponent;

    private final static String SHOW_MESSAGE = "예매 목록을 불러옵니다.";
    private final static String CANCEL_MESSAGE = "예매가 취소되었습니다.";
    private final static String CHANGE_MESSAGE = "예매 정보를 수정했습니다. 예매 티켓 수가 변동됩니다.";

    private Long possibleReservationDate;
    private final static Long VIP_POSSIBLE_DATE = 7L;
    private final static Long STANDARD_POSSIBLE_DATE = 6L;

    @Transactional
    @Override
    public Reservation makeReservation(Long performanceId, Long performanceScheduleId, ReservationRequestDto dto,
        Authentication auth) {

        String email = auth.getName();
        Integer ticket = Integer.parseInt(dto.getReservedTicketNum());

        /** 예매 요청 티켓수가 1 또는 2매인지 확인 */
        checkReservationTicketNum(ticket);

        Member member = memberRepository.findByEmail(email).orElseThrow(NotExistSuitableDataException::new);
        RatingName ratingName = member.getRating().getName();

        PerformanceSchedule performanceSchedule = performanceScheduleRepository.findById(performanceScheduleId)
            .orElseThrow(NotExistSuitableDataException::new);

        /** 공연 정보가 올바른지 확인 */
        checkPerformance(performanceSchedule, performanceId);

        /** 이미 예매한 정보가 있는지 확인 */
        existReservation(member, performanceSchedule);

        /** 예매 가능한 상태인지 확인 - performance_state == TICKETING */
        checkPerformanceState(performanceSchedule);

        /** 예매 가능한 날짜인지 확인 */
        checkReservationPossibleDate(performanceSchedule, ratingName);

        /** 티켓 남아있는지 확인 후 티켓 수량 변경 */
        updateTicket(performanceSchedule.getId(), ticket, ratingName);

        Reservation reservation = Reservation.builder()
            .member(member)
            .performanceSchedule(performanceSchedule)
            .performanceName(performanceSchedule.getPerformance().getName())
            .performanceDate(performanceSchedule.getPerformanceDate())
            .reservedTicketNum(Integer.parseInt(dto.getReservedTicketNum()))
            .reservationStatus(ReservationStatus.YES)
            .reservationDate(LocalDateTime.now())
            .build();

        return reservationRepository.save(reservation);
    }

    // 예매 완료 후 확인 메일 보내기
    @Override
    public void sendMail(String email, Reservation savedReservation) {
        if (reservationRepository.findById(savedReservation.getId()).isEmpty()) {
            throw new NotExistSuitableDataException();
        }

        String subject = "[공연 예매 사이트] 예매가 무사히 완료되었습니다.";
        StringBuilder text = new StringBuilder();
        text.append(email).append("님의 [").append(savedReservation.getPerformanceName())
            .append("] 예매가 무사히 완료되었습니다. 자세한 내용은 예매목록보기에서 확인할 수 있습니다.");

        mailComponent.sendMail(email, subject, text);

    }

    // 예매 목록 보기
    @Transactional
    @Override
    public ResponseDto showReservations(Authentication auth, Pageable pageable) {
        List<ReservationResponseDto> dtoList = new ArrayList<>();

        String email = auth.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(NotExistSuitableDataException::new);

        Page<Reservation> list = reservationRepository.findAllByMember(member, pageable);

        for (Reservation savedReservation : list) {
            ReservationResponseDto dto = new ReservationResponseDto(
                savedReservation.getPerformanceSchedule().getPerformance().getName(),
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

        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(NotExistSuitableDataException::new);
        String email = auth.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(NotExistSuitableDataException::new);

        /** 예매 정보의 회원정보와 로그인 회원 일치 여부 확인 */
        checkReservationMemberMatch(reservation, member);

        /** 예매가 되어있는지 확인*/
        checkCanceledReservation(reservation);

        reservation.updateReservationStatus(ReservationStatus.NO);

        /** 취소 표 반환하기 */
        PerformanceSchedule performanceSchedule = performanceScheduleRepository
            .findById(reservation.getPerformanceSchedule().getId()).orElseThrow(NotExistSuitableDataException::new);

        performanceSchedule.updateTicketQuantity
            (reservation.getReservedTicketNum() + performanceSchedule.getTicketQuantity());

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(CANCEL_MESSAGE)
            .body(new ReservationResponseDto(
                reservation.getPerformanceSchedule().getPerformance().getName(),
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
        Member member = memberRepository.findByEmail(email).orElseThrow(NotExistSuitableDataException::new);

        Reservation reservation = reservationRepository.findById(dto.getReservationId())
            .orElseThrow(NotExistSuitableDataException::new);

        /** 예매 정보의 회원정보와 로그인 회원 일치 여부 확인 */
        checkReservationMemberMatch(reservation, member);

        /** 예매가 되어있는지 확인*/
        checkCanceledReservation(reservation);

        if (!reservation.getReservedTicketNum().equals(newTicketNum)) {
            PerformanceSchedule performanceSchedule =
                performanceScheduleRepository.findById(reservation.getPerformanceSchedule().getId())
                    .orElseThrow(NotExistSuitableDataException::new);

            performanceSchedule.updateTicketQuantity(
                performanceSchedule.getTicketQuantity() + reservation.getReservedTicketNum());

            updateTicket(reservation.getPerformanceSchedule().getId(), Integer.parseInt(dto.getReservedTicketNum()),
                member.getRating().getName());

            reservation.updateReservedTicketNum(Integer.parseInt(dto.getReservedTicketNum()));
        }

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(CHANGE_MESSAGE)
            .body(new ReservationResponseDto(
                reservation.getPerformanceSchedule().getPerformance().getName(),
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
    public void updateTicketForStandard(PerformanceSchedule performanceSchedule, Integer ticket) {

        int leftTicket = performanceSchedule.getTicketQuantity() - ticket;

        if (leftTicket < 0) {
            throw new NotPresentTicketException();
        }

        performanceSchedule.updateTicketQuantity(leftTicket);

    }

    @Transactional
    @Override
    public void updateTicket(Long performanceScheduleId, Integer ticket, RatingName ratingName) {

        PerformanceSchedule performanceSchedule = performanceScheduleRepository.findById(performanceScheduleId)
            .orElseThrow(NotExistSuitableDataException::new);

        int leftTicket = performanceSchedule.getTicketQuantity() - ticket;
        int ticketForVip = performanceSchedule.getInitialTicketQuantity() * 8 / 10;

        if (ratingName.equals(RatingName.VIP)) {
            if (ticketForVip > leftTicket) {
                log.info("vip티켓은 구매 못함");
            } else {
                updateTicketForStandard(performanceSchedule, ticket);
                log.info("vip 티켓 예매");
                return;
            }

            if (performanceSchedule.getPerformanceDate().minusDays(STANDARD_POSSIBLE_DATE)
                .withHour(0).withMinute(0).isBefore(LocalDateTime.now())) {

                log.info("vip회원 일반 티켓 예매");
                updateTicketForStandard(performanceSchedule, ticket);

            } else {
                throw new CanNotVipReservationException();
            }
        } else if (ratingName.equals(RatingName.STANDARD)) {
            log.info("일반 회원 일반 티켓 예매");
            updateTicketForStandard(performanceSchedule, ticket);
        }

    }

    //올바른 공연정보인지 확인
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
    public void checkReservationPossibleDate(PerformanceSchedule performanceSchedule, RatingName ratingName) {
        if (ratingName.equals(RatingName.VIP)) {
            possibleReservationDate = VIP_POSSIBLE_DATE;
        } else if (ratingName.equals(RatingName.STANDARD)) {
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
    public void checkReservationMemberMatch(Reservation reservation, Member member) {
        if (!member.equals(reservation.getMember())) {
            throw new NotValidReservationMemberException();
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
