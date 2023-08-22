package com.starter.performance.repository;

import com.starter.performance.domain.Member;
import com.starter.performance.domain.Reservation;
import com.starter.performance.domain.Review;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByReservationAndMember(Reservation reservation, Member member);

}
