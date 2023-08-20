package com.starter.performance.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//Member 엔티티와 Review 엔티티는 단방향으로, 회원 테이블만 후기 테이블 참조하면 되겠다.
//Review 엔티티가 Reservation 엔티티를 단방향으로 참조하면 되겠다. -> 안된다는데? 양방향으로 구현? 외래키 있는 테이블이 주
@Entity
@Builder
@Getter
//@RequiredArgsConstructor  //Builder 사용하면 충돌남
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Column(nullable = false)
    private String reviewTitle;

    @Column(nullable = false)
    private String reviewContent;

}
