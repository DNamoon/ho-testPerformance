package com.starter.performance.service.dto;

import com.starter.performance.domain.ReservationStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationInfoDto {
    private String performanceName;
    private Integer reservedTicketNum;
    private ReservationStatus reservationStatus;
    private LocalDateTime performanceDate;
    private LocalDateTime reservationDate;
    private LocalDateTime cancelDate;
}
