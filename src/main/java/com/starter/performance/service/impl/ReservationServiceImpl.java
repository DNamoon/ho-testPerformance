package com.starter.performance.service.impl;

import com.starter.performance.domain.Reservation;
import com.starter.performance.repository.ReservationRepository;
import com.starter.performance.service.ReservationService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;


    @Override
    public Optional<Reservation> findOne(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }
}
