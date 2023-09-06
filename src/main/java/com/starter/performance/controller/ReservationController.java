package com.starter.performance.controller;

import com.starter.performance.controller.dto.ChangeReservationDto;
import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.controller.dto.ResponseDto;
import com.starter.performance.service.ReservationService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @PostMapping("/performances/{performanceId}/{performanceScheduleId}/reservation")
    public ResponseEntity<ResponseDto> createReservation(@PathVariable Long performanceScheduleId,
        @PathVariable Long performanceId, @RequestBody @Valid ReservationRequestDto dto,
        Authentication auth) {
        ResponseDto responseDto = reservationService.makeReservation(performanceId, performanceScheduleId, dto, auth);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/reservations")
    public ResponseEntity<ResponseDto> viewReservations(Authentication auth,
        @PageableDefault(size = 7) Pageable pageable) {
        ResponseDto responseDto = reservationService.showReservations(auth, pageable);
        return ResponseEntity.ok(responseDto);
    }

    // 예매 취소하기
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<ResponseDto> deleteReservation(Authentication auth,
        @PathVariable Long reservationId) {
        ResponseDto responseDto = reservationService.cancelReservation(auth, reservationId);
        return ResponseEntity.ok(responseDto);
    }

    // 예매 수정하기 - 만들긴 했는데 예매 취소하고 다시 예매하게 하자.
//    @PutMapping("/reservations")
//    public ResponseEntity<ResponseDto> changeReservation(Authentication auth,
//        @RequestBody @Valid ChangeReservationDto dto) {
//        ResponseDto responseDto = reservationService.changeReservation(auth, dto);
//        return ResponseEntity.ok(responseDto);
//    }
}
