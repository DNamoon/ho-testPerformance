package com.starter.performance.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
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
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member memberId;

    @ManyToOne
    @JoinColumn(name = "performance_Schedule_id")
    private PerformanceSchedule performanceSchedule;

    private String performanceName;

    private Integer ticketQuantity;

    /** 이거 enum으로 하려다가 에러 자꾸 남. 타입 ReservatioinStatus 에서 String으로 변경*/
    //For input string: "YES"; nested exception is java.lang.NumberFormatException 에러 발생
    private String reservationStatus;

    private LocalDateTime performanceDate;

    private LocalDateTime reservationDate;

}
