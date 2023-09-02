package com.starter.performance.repository;

import com.starter.performance.domain.Member;
import com.starter.performance.domain.PerformanceSchedule;
import com.starter.performance.domain.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByMemberAndPerformanceSchedule(Member member, PerformanceSchedule performanceSchedule);

    Page<Reservation> findAllByMember(Member member, Pageable pageable);
}
