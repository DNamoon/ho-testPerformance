package com.starter.performance.repository;

import com.starter.performance.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

//    Reservation findById(Long id);

    //enum 파일 에러 나는 것 같아서 String으로 변경
//    PerformanceStatus findByPerformanceSchedule_PerformanceStatus(Long performanceScheduleId);
    String findByPerformanceSchedule_PerformanceStatus(Long performanceScheduleId);

    Long findByPerformanceSchedule(Long reservationId);
//    @Query("select r.performanceSchedule.performanceStatus from Reservation r join FETCH r.performanceSchedule")
//    PerformanceStatus checkPerformanceStatus(@Param("reservationId") Long reservationId);
}
