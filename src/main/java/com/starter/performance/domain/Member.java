package com.starter.performance.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener.class)
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @Column(unique = true, length = 50, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "phone_number", nullable = false)
  private String phoneNumber;

  @Column(nullable = false)
  private String nickname;

  @Column(name = "registered_date", nullable = false)
  @CreatedDate
  @NotNull
  private LocalDateTime registeredDate;

  private LocalDateTime modifiedDate;

  private LocalDateTime withdrawalDate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Permission permission;

  private boolean emailAuth;

  private boolean sanctionWhether;

  @Builder
  public Member(String email, String password, String phoneNumber,
      String nickname, Permission permission) {
    this.email = email;
    this.password = password;
    this.phoneNumber = phoneNumber;
    this.nickname = nickname;
    this.permission = Permission.MEMBER;
  }
}