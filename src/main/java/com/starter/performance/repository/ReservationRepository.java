package com.starter.performance.repository;

import com.starter.performance.domain.PerformanceSchedule;
import com.starter.performance.domain.PerformanceStatus;
import com.starter.performance.domain.Reservation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Reservation findByReservationId(Long reservationId);

    //enum 파일 에러 나는 것 같아서 String으로 변경
//    PerformanceStatus findByPerformanceSchedule_PerformanceStatus(Long performanceScheduleId);
    String findByPerformanceSchedule_PerformanceStatus(Long performanceScheduleId);

    Long findByPerformanceSchedule(Long reservationId);
//    @Query("select r.performanceSchedule.performanceStatus from Reservation r join FETCH r.performanceSchedule")
//    PerformanceStatus checkPerformanceStatus(@Param("reservationId") Long reservationId);
}
