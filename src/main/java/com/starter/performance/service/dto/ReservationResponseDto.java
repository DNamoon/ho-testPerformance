package com.starter.performance.service.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationResponseDto {

    private String performanceName;
    private Integer reservedTicketNum;
    private LocalDateTime performanceDate;
    private LocalDateTime reservationDate;
}
