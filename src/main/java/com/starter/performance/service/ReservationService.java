package com.starter.performance.service;

import com.starter.performance.controller.dto.ReservationRequestDto;
import com.starter.performance.service.dto.ResponseDto;
import org.springframework.security.core.Authentication;

public interface ReservationService {

    ResponseDto makeReservation(Long performanceScheduleId, ReservationRequestDto dto, Authentication auth);
}
