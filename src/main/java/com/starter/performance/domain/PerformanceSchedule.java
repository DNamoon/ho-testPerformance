package com.starter.performance.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime performanceDate;
    private int ticketQuantity;
    private int initialTicketQuantity;
    @Enumerated(EnumType.STRING)
    private PerformanceStatus performanceStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance")
    private Performance performance;

    @Builder
    private PerformanceSchedule(Long id, LocalDateTime performanceDate, int ticketQuantity,
        PerformanceStatus performanceStatus, Performance performance) {
        this.id = id;
        this.performanceDate = performanceDate;
        this.ticketQuantity = ticketQuantity;
        this.initialTicketQuantity = ticketQuantity;
        this.performanceStatus = PerformanceStatus.STAND_BY;
        this.performance = performance;
    }

    public void updatePerformanceDate(LocalDateTime performanceDate) {
        this.performanceDate = performanceDate;
    }

    public void updateTicketQuantity(int ticketQuantity) {
        this.ticketQuantity = ticketQuantity;
    }

}
