package com.starter.performance.controller;

import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.controller.dto.ResponseDto;
import com.starter.performance.domain.Reservation;
import com.starter.performance.service.ReservationService;
import com.starter.performance.service.dto.ReservationResponseDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    private final static String RESERVATION_MESSAGE = "예매가 완료되었습니다.";

    @PostMapping("/performances/{performanceId}/{performanceScheduleId}/reservation")
    public ResponseEntity<ResponseDto> createReservation(@PathVariable Long performanceScheduleId,
        @PathVariable Long performanceId, @RequestBody @Valid ReservationRequestDto dto,
        Authentication auth) {
        Reservation savedReservation =
            reservationService.makeReservation(performanceId, performanceScheduleId, dto, auth);

        reservationService.sendMail(auth.getName(), savedReservation);

        ResponseDto responseDto = ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(RESERVATION_MESSAGE)
            .body(new ReservationResponseDto(
                savedReservation.getPerformanceSchedule().getPerformance().getName(),
                savedReservation.getReservedTicketNum(),
                savedReservation.getReservationStatus(),
                savedReservation.getPerformanceSchedule().getPerformanceDate(),
                savedReservation.getReservationDate()
            ))
            .build();

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/reservations")
    public ResponseEntity<ResponseDto> viewReservations(Authentication auth,
        @PageableDefault(size = 5) Pageable pageable) {
        ResponseDto responseDto = reservationService.showReservations(auth, pageable);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<ResponseDto> deleteReservation(Authentication auth,
        @PathVariable Long reservationId) {
        ResponseDto responseDto = reservationService.cancelReservation(auth, reservationId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/reservations/{reservationId}")
    public ResponseEntity<ResponseDto> changeReservation(Authentication auth,
        @RequestBody @Valid ReservationRequestDto dto, @PathVariable Long reservationId) {
        ResponseDto responseDto = reservationService.changeReservation(auth, dto, reservationId);
        return ResponseEntity.ok(responseDto);
    }

}
