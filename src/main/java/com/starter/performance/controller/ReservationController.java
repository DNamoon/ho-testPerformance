package com.starter.performance.controller;

import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.service.ReservationService;
import com.starter.performance.controller.dto.ResponseDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/performance/performanceId/{performanceScheduleId}/reservations")
    public ResponseEntity<ResponseDto> createReservation(@PathVariable Long performanceScheduleId,
        @RequestBody @Valid ReservationRequestDto dto,
        Authentication auth) {
        ResponseDto responseDto = reservationService.makeReservation(performanceScheduleId, dto, auth);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/reservations")
    public ResponseEntity<ResponseDto> viewReservations(Authentication auth,
        @PageableDefault(size = 7) Pageable pageable) {
        ResponseDto responseDto = reservationService.showReservations(auth, pageable);
        return ResponseEntity.ok(responseDto);
    }
}
