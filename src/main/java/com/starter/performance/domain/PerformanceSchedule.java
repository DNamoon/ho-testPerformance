package com.starter.performance.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class PerformanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performanceScheduleId;

    @ManyToOne
    @JoinColumn(name = "performanceId")
    private Performance performanceId;

    private LocalDateTime performanceDate;

    private Integer ticket_quantity;

    /** enum으로 하려니까 에러남. 타입 PerformanceStatus에서 String으로 변경*/
    private String performanceStatus;
}
