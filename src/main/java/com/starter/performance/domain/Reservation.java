package com.starter.performance.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "performance_schedule")
    private PerformanceSchedule performanceSchedule;

    @Column(nullable = false)
    private Integer reservedTicketNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReservationStatus reservationStatus;

    @Column(nullable = false)
    private LocalDateTime reservationDate;

    private LocalDateTime cancelDate;

    public void updateReservedTicketNum(Integer reservedTicketNum) {
        this.reservedTicketNum = reservedTicketNum;
    }

    public void updateReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public void updateReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public void updateCancelDate(LocalDateTime cancelDate) {
        this.cancelDate = cancelDate;
    }
}
